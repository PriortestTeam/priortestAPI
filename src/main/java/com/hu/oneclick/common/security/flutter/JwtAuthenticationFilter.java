package com.hu.oneclick.common.security.flutter;

import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.hu.oneclick.common.constant.OneConstant.REDIS_KEY_PREFIX;
import com.hu.oneclick.common.security.ApiToken;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.entity.SysUserToken;
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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        // 默认不处理任何请求
        this.requiresAuthenticationRequestMatcher = request -> false;
        // 初始化白名单，添加登录接口
        this.permissiveRequestMatchers = new ArrayList<>();
        setPermissiveUrl("/login", "/register", "/logout");
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
        Assert.notNull(successHandler, "AuthenticationSuccessHandler must be specified");
        Assert.notNull(failureHandler, "AuthenticationFailureHandler must be specified");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        System.out.println(">>> JwtAuthenticationFilter 收到请求: " + requestPath + ", Authorization: " + request.getHeader("Authorization"));

        // 如果是白名单URL，直接放行
        if (permissiveRequestMatchers != null) {
            for (RequestMatcher matcher : permissiveRequestMatchers) {
                if (matcher.matches(request)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        // 获取认证头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token found in request headers");
            return;
        }

        try {
            // 检查是否是API Token（不以Bearer开头的token）
            if (!authHeader.startsWith("Bearer ")) {
                // API Token处理
                String emailId = request.getHeader("emailId");
                if (StringUtils.isBlank(emailId)) {
                    throw new InsufficientAuthenticationException("emailId is required for API token");
                }

                // 检查是否是apiAdpater路径
                boolean isApiAdapterPath = requestPath.toLowerCase().contains("apiadpater");
                System.out.println(">>> 检查API路径: " + requestPath + ", 是否匹配: " + isApiAdapterPath);
                if (!isApiAdapterPath) {
                    throw new InsufficientAuthenticationException("API token can only access apiAdpater endpoints");
                }

                SysUserToken sysUserToken = sysUserTokenDao.selectByTokenValue(authHeader);
                if (sysUserToken == null) {
                    throw new InsufficientAuthenticationException("Invalid API token");
                }

                // 检查token是否过期
                if (sysUserToken.getExpirationTime().before(new Date())) {
                    throw new InsufficientAuthenticationException("API token has expired");
                }

                // 验证用户账号
                if (!userService.getUserAccountInfo(emailId, authHeader)) {
                    throw new InsufficientAuthenticationException("Invalid emailId or token");
                }

                // 设置认证信息
                Authentication authentication = new ApiToken(true, sysUserToken.getTokenName());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 缓存用户信息
                AuthLoginUser authLoginUser = (AuthLoginUser) userDetailsService.loadUserByUsername(emailId);
                Map<String, Object> map = new HashMap<>();
                map.put(REDIS_KEY_PREFIX.LOGIN + sysUserToken.getTokenName(), JSONObject.toJSONString(authLoginUser));
                redisClient.getBuckets().set(map);

                filterChain.doFilter(request, response);
                return;
            }

            // JWT Token处理
            String token = authHeader.substring(7); // 移除"Bearer "前缀
            if (StringUtils.isBlank(token)) {
                throw new InsufficientAuthenticationException("JWT is Empty");
            }

            try {
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));
                Authentication authResult = this.getAuthenticationManager().authenticate(authToken);
                String username = ((AuthLoginUser) authResult.getPrincipal()).getUsername();
                RBucket<String> bucket = redisClient.getBucket(REDIS_KEY_PREFIX.LOGIN_JWT + username);
                String redisToken = bucket.get();
                
                if (!StringUtils.equals(redisToken, token)) {
                    throw new InsufficientAuthenticationException("JWT token invalid or user logged out");
                }
                
                SecurityContextHolder.getContext().setAuthentication(authResult);
                filterChain.doFilter(request, response);
            } catch (JWTDecodeException e) {
                throw new InsufficientAuthenticationException("Invalid JWT format", e);
            }
        } catch (AuthenticationException e) {
            unsuccessfulAuthentication(request, response, e);
        }
    }

    protected String getJwtToken(HttpServletRequest request) {
        String authInfo = request.getHeader("Authorization");
        return StringUtils.removeStart(authInfo, "Bearer ");
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        System.out.println(">>> 登录失败，异常信息: " + failed.getMessage());
        failureHandler.onAuthenticationFailure(request, response, failed);
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
            if (!url.startsWith("/api/")) {
                permissiveRequestMatchers.add(new AntPathRequestMatcher("/api" + url));
            }
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }
}