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
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher("Authorization");
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
        System.out.println(">>> JwtAuthenticationFilter 收到请求: " + request.getRequestURI() + ", Authorization: " + request.getHeader("Authorization"));

        // First check if this is a permissive URL - if so, skip JWT validation entirely
        if (permissiveRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getJwtToken(request);
            if (StringUtils.isBlank(token)) {
                throw new InsufficientAuthenticationException("JWT is Empty");
            }

            // Try JWT token validation first
            try {
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));
                Authentication authResult = this.getAuthenticationManager().authenticate(authToken);
                String username = ((AuthLoginUser) authResult.getPrincipal()).getUsername();
                RBucket<String> bucket = redisClient.getBucket(REDIS_KEY_PREFIX.LOGIN_JWT + username);
                String redisToken = bucket.get();
                
                if (!StringUtils.equals(redisToken, token)) {
                    throw new InsufficientAuthenticationException("Token invalid or user logged out");
                }
                
                SecurityContextHolder.getContext().setAuthentication(authResult);
                filterChain.doFilter(request, response);
                return;
            } catch (JWTDecodeException e) {
                // If JWT validation fails, try database token
                SysUserToken sysUserToken = sysUserTokenDao.selectByTokenValue(token);
                System.out.println(">>> sysUserTokenDao.selectByTokenValue 结果: " + sysUserToken);

                if (sysUserToken != null) {
                    Date expirationTime = sysUserToken.getExpirationTime();
                    if (expirationTime.before(new Date())) {
                        throw new InsufficientAuthenticationException("Token has expired");
                    }

                    // Handle API token authentication
                    String emailId = request.getHeader("emailId");
                    if (StringUtils.isNotBlank(emailId)) {
                        if (!userService.getUserAccountInfo(emailId, token)) {
                            throw new InsufficientAuthenticationException("Authentication failed");
                        }
                    }

                    Authentication authentication = new ApiToken(true, sysUserToken.getTokenName());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Cache user info in Redis if emailId is present
                    if (StringUtils.isNotEmpty(emailId)) {
                        AuthLoginUser authLoginUser = (AuthLoginUser) userDetailsService.loadUserByUsername(emailId);
                        Map<String, Object> map = new HashMap<>();
                        map.put(REDIS_KEY_PREFIX.LOGIN + sysUserToken.getTokenName(), JSONObject.toJSONString(authLoginUser));
                        redisClient.getBuckets().set(map);
                    }
                    
                    filterChain.doFilter(request, response);
                    return;
                }
                
                throw new InsufficientAuthenticationException("Invalid token format", e);
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
            // Also add the /api prefixed version
            if (!url.startsWith("/api/")) {
                permissiveRequestMatchers.add(new AntPathRequestMatcher("/api" + url));
            }
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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