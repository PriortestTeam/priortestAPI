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
        System.out.println(">>> 初始化JwtAuthenticationFilter，白名单URLs: " + permissiveRequestMatchers);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
        Assert.notNull(successHandler, "AuthenticationSuccessHandler must be specified");
        Assert.notNull(failureHandler, "AuthenticationFailureHandler must be specified");
        System.out.println(">>> JwtAuthenticationFilter.afterPropertiesSet() 完成初始化");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        System.out.println(">>> [" + method + "] JwtAuthenticationFilter 收到请求: " + requestPath);
        System.out.println(">>> 请求头信息:");
        System.out.println(">>>   Authorization: " + request.getHeader("Authorization"));
        System.out.println(">>>   emailId: " + request.getHeader("emailId"));

        // 如果是白名单URL，直接放行
        System.out.println(">>> 检查是否是白名单URL...");
        if (permissiveRequestMatchers != null) {
            for (RequestMatcher matcher : permissiveRequestMatchers) {
                System.out.println(">>>   检查matcher: " + matcher);
                if (matcher.matches(request)) {
                    System.out.println(">>>   匹配到白名单URL，直接放行");
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        System.out.println(">>> 不是白名单URL，继续处理...");

        // 获取认证头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.println(">>> 没有找到Authorization头，返回401错误");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token found in request headers");
            return;
        }

        try {
            // 检查是否是API Token（不以Bearer开头的token）
            if (!authHeader.startsWith("Bearer ")) {
                System.out.println(">>> 检测到API Token认证方式");
                // API Token处理
                String emailId = request.getHeader("emailId");
                if (StringUtils.isBlank(emailId)) {
                    System.out.println(">>> API Token认证失败：缺少emailId");
                    throw new InsufficientAuthenticationException("emailId is required for API token");
                }

                // 检查是否是apiAdpater路径
                boolean isApiAdapterPath = requestPath.toLowerCase().contains("apiadpater");
                System.out.println(">>> 检查API路径: " + requestPath);
                System.out.println(">>>   是否包含'apiadpater': " + isApiAdapterPath);
                if (!isApiAdapterPath) {
                    System.out.println(">>> API Token认证失败：非apiAdpater路径");
                    throw new InsufficientAuthenticationException("API token can only access apiAdpater endpoints");
                }

                SysUserToken sysUserToken = sysUserTokenDao.selectByTokenValue(authHeader);
                System.out.println(">>> 查询数据库中的token: " + (sysUserToken != null ? "找到" : "未找到"));
                if (sysUserToken == null) {
                    System.out.println(">>> API Token认证失败：无效的token");
                    throw new InsufficientAuthenticationException("Invalid API token");
                }

                // 检查token是否过期
                if (sysUserToken.getExpirationTime().before(new Date())) {
                    System.out.println(">>> API Token认证失败：token已过期");
                    throw new InsufficientAuthenticationException("API token has expired");
                }

                // 验证用户账号
                boolean accountValid = userService.getUserAccountInfo(emailId, authHeader);
                System.out.println(">>> 验证用户账号: " + (accountValid ? "成功" : "失败"));
                if (!accountValid) {
                    System.out.println(">>> API Token认证失败：用户账号验证失败");
                    throw new InsufficientAuthenticationException("Invalid emailId or token");
                }

                // 设置认证信息
                Authentication authentication = new ApiToken(true, sysUserToken.getTokenName());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println(">>> API Token认证成功，设置认证信息完成");

                // 缓存用户信息
                AuthLoginUser authLoginUser = (AuthLoginUser) userDetailsService.loadUserByUsername(emailId);
                Map<String, Object> map = new HashMap<>();
                map.put(REDIS_KEY_PREFIX.LOGIN + sysUserToken.getTokenName(), JSONObject.toJSONString(authLoginUser));
                redisClient.getBuckets().set(map);
                System.out.println(">>> 用户信息已缓存到Redis");

                filterChain.doFilter(request, response);
                return;
            }

            System.out.println(">>> 检测到JWT Bearer Token认证方式");
            // JWT Token处理
            String token = authHeader.substring(7); // 移除"Bearer "前缀
            if (StringUtils.isBlank(token)) {
                System.out.println(">>> JWT认证失败：token为空");
                throw new InsufficientAuthenticationException("JWT is Empty");
            }

            try {
                System.out.println(">>> 开始验证JWT token");
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));
                Authentication authResult = this.getAuthenticationManager().authenticate(authToken);
                String username = ((AuthLoginUser) authResult.getPrincipal()).getUsername();
                System.out.println(">>> JWT token解析成功，用户名: " + username);

                RBucket<String> bucket = redisClient.getBucket(REDIS_KEY_PREFIX.LOGIN_JWT + username);
                String redisToken = bucket.get();
                System.out.println(">>> 从Redis获取token: " + (redisToken != null ? "成功" : "未找到"));
                
                if (!StringUtils.equals(redisToken, token)) {
                    System.out.println(">>> JWT认证失败：Redis中的token与请求token不匹配或已失效");
                    throw new InsufficientAuthenticationException("JWT token invalid or user logged out");
                }
                
                SecurityContextHolder.getContext().setAuthentication(authResult);
                System.out.println(">>> JWT认证成功，设置认证信息完成");
                filterChain.doFilter(request, response);
            } catch (JWTDecodeException e) {
                System.out.println(">>> JWT认证失败：token格式错误");
                throw new InsufficientAuthenticationException("Invalid JWT format", e);
            }
        } catch (AuthenticationException e) {
            System.out.println(">>> 认证异常: " + e.getMessage());
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
        System.out.println(">>> 认证失败处理开始");
        System.out.println(">>> 错误信息: " + failed.getMessage());
        System.out.println(">>> 错误类型: " + failed.getClass().getSimpleName());
        if (failed.getCause() != null) {
            System.out.println(">>> 原始错误: " + failed.getCause().getMessage());
        }
        failureHandler.onAuthenticationFailure(request, response, failed);
        System.out.println(">>> 认证失败处理完成");
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
        System.out.println(">>> 添加白名单URLs:");
        for (String url : urls) {
            System.out.println(">>>   原始URL: " + url);
            permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
            if (!url.startsWith("/api/")) {
                String apiUrl = "/api" + url;
                System.out.println(">>>   添加API版本: " + apiUrl);
                permissiveRequestMatchers.add(new AntPathRequestMatcher(apiUrl));
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