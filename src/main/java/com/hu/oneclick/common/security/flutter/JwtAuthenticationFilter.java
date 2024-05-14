package com.hu.oneclick.common.security.flutter;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.hu.oneclick.common.constant.OneConstant.REDIS_KEY_PREFIX;
import com.hu.oneclick.common.security.ApiToken;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUserToken;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.server.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author qingyang
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private List<RequestMatcher> permissiveRequestMatchers;
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;
    @Autowired
    private RedissonClient redisClient;
    @Autowired
    private UserDetailsService userDetailsService;

    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public JwtAuthenticationFilter() {
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher("Authorization");
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
        Assert.notNull(successHandler, "AuthenticationSuccessHandler must be specified");
        Assert.notNull(failureHandler, "AuthenticationFailureHandler must be specified");
    }

    protected String getJwtToken(HttpServletRequest request) {
        String authInfo = request.getHeader("Authorization");
        return StringUtils.removeStart(authInfo, "Bearer ");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        SysUserToken sysUserToken = sysUserTokenDao.selectByTokenValue(authorization);
        if (!org.springframework.util.StringUtils.isEmpty(sysUserToken)) {
            Date expirationTime = sysUserToken.getExpirationTime();
            if (expirationTime.before(new Date())) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    JSONObject.toJSONString(new Resp.Builder<String>().buildResult("token已过期")));
                return;
            }
            Authentication authentication = new ApiToken(true, sysUserToken.getTokenName());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //第三方调用api
            String emailId = request.getHeader("emailId");
            if (StringUtils.isNotBlank(emailId)) {
                if (!userService.getUserAccountInfo(emailId)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        JSONObject.toJSONString(new Resp.Builder<String>().buildResult("权限认证失败")));
                    return;
                }
            } else {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    JSONObject.toJSONString(new Resp.Builder<String>().buildResult("必须填写emailid")));
                return;
            }
        } else {
            Authentication authResult = null;
            AuthenticationException failed = null;
            if (!requiresAuthentication(request, response)) {
                //过滤不用认证的请求
                if (permissiveRequest(request)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                //否则失败
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    JSONObject.toJSONString(new Resp.Builder<String>().buildResult("请填写账号密码")));
                return;
            }
            try {
                String token = getJwtToken(request);

                if (StringUtils.isNotBlank(token)) {
                    JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));
                    authResult = this.getAuthenticationManager().authenticate(authToken);
                    // 这里添加读取redis逻辑，如果有这个token，继续往下走，如果没有，则认为失效，或者有其他设备登陆被踢了
                    String username = ((AuthLoginUser)authResult.getPrincipal()).getUsername();
                    RBucket<String> bucket = redisClient.getBucket(REDIS_KEY_PREFIX.LOGIN_JWT + username);
                    String redisToken = bucket.get();
                    if (!StringUtils.equals(redisToken, token)) {
                        authResult = null;
                    }
                } else {
                    failed = new InsufficientAuthenticationException("JWT is Empty");
                }
            } catch (JWTDecodeException e) {
                failed = new InsufficientAuthenticationException("JWT format error", null);
            } catch (AuthenticationException e) {
                failed = e;
                logger.error(e);
            }
            if (authResult != null) {
                successfulAuthentication(request, response, filterChain, authResult);
            } else if (!permissiveRequest(request)) {
                unsuccessfulAuthentication(request, response, failed);
                return;
            }
        }
        //权限认证通过后把当前登录人信息放入redis缓存
        AuthLoginUser authLoginUser = (AuthLoginUser) userDetailsService.loadUserByUsername(request.getHeader("emailid"));
        Map<String,Object> map = new HashMap<>();
        map.put(REDIS_KEY_PREFIX.LOGIN+sysUserToken.getTokenName(),authLoginUser);
        redisClient.getBuckets().set(map);
        filterChain.doFilter(request, response);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    protected boolean requiresAuthentication(HttpServletRequest request,
        HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    protected boolean permissiveRequest(HttpServletRequest request) {
        if (permissiveRequestMatchers == null) {
            return false;
        }
        for (RequestMatcher permissiveMatcher : permissiveRequestMatchers) {
            if (permissiveMatcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    public void setPermissiveUrl(String... urls) {
        if (permissiveRequestMatchers == null) {
            permissiveRequestMatchers = new ArrayList<>();
        }
        for (String url : urls) {
            permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
        }
    }

    public void setAuthenticationSuccessHandler(
        AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(
        AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

}
