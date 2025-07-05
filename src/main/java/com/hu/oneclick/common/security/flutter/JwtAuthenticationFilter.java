package com.hu.oneclick.common.security.flutter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.entity.SysUserToken;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.server.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qingyang
 * JWT认证过滤器，支持两种Token认证方式：
 * 1. Bearer Token: 用户登录后生成的JWT token，用于前端应用访问
 * 2. API Token: 用户生成的token，用于API访问，格式：emailId + token
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;
    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private List<RequestMatcher> permissiveRequestMatchers = new ArrayList<>();

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Autowired
    private RedissonClient redissonClient;

    public JwtAuthenticationFilter() {
        // 设置默认的白名单URL
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/login"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/register"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/logout"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/swagger-ui/**"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/v3/api-docs/**"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/swagger-resources/**"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/webjars/**"));
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

        System.out.println(">>> [" + request.getMethod() + "] JwtAuthenticationFilter 收到请求: " + request.getRequestURI());
        String path = request.getRequestURI();

        // 检查是否是需要跳过JWT验证的路径（登录、Swagger等）
        if (isPermissivePath(path)) {
            System.out.println(">>> 跳过JWT验证，直接放行请求: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // 检查是否是白名单URL
        for (RequestMatcher matcher : permissiveRequestMatchers) {
            if (matcher.matches(request)) {
                System.out.println(">>> 是白名单URL，直接放行: " + path);
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            Authentication authentication = attemptAuthentication(request, response, filterChain);
            if (authentication != null) {
                System.out.println(">>> 认证成功，设置SecurityContext");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                successHandler.onAuthenticationSuccess(request, response, authentication);
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            System.out.println(">>> 认证失败: " + e.getMessage());
            SecurityContextHolder.clearContext();
            failureHandler.onAuthenticationFailure(request, response, e);
        }
    }

    private boolean isPermissivePath(String path) {
        return path.equals("/api/login") || 
               path.equals("/api/register") || 
               path.equals("/api/logout") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars");
    }

    /**
     * 尝试进行认证，支持两种Token方式
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws AuthenticationException, IOException {

        String path = request.getRequestURI();
        System.out.println(">>> 开始认证，请求路径: " + path);

        // 检查是否是API适配器路径 - 使用emailId:token认证方式
        if (path.contains("/api/apiAdapter")) {
            System.out.println(">>> 检测到API适配器路径，使用emailId:token认证");
            return attemptApiTokenAuthentication(request);
        } else {
            // 其他路径使用Bearer Token认证
            System.out.println(">>> 使用Bearer Token认证");
            return attemptBearerTokenAuthentication(request);
        }
    }

    /**
     * API Token认证 (emailId:token格式)
     */
    private Authentication attemptApiTokenAuthentication(HttpServletRequest request) throws AuthenticationException {
        String emailId = request.getHeader("emailId");
        String token = request.getHeader("Authorization");

        if (StringUtils.isNotBlank(emailId) && StringUtils.isNotBlank(token)) {
            System.out.println(">>> 检测到emailId和token认证方式");

            // 先验证用户账号是否存在
            Boolean isValid = userService.getUserAccountInfo(emailId, null);
            if (isValid == null || !isValid) {
                System.out.println(">>> 用户账号不存在");
                throw new BadCredentialsException("User not found");
            }

            // 验证token是否存在于数据库中
            SysUserToken sysUserToken = sysUserTokenDao.selectByTokenValue(token);
            if (sysUserToken == null) {
                System.out.println(">>> 用户token不存在");
                throw new BadCredentialsException("Token not found");
            }
            if (isValid == null || !isValid) {
                System.out.println(">>> 用户登录信息不存在或token无效");
                throw new BadCredentialsException("User not found or invalid token");
            }

            // 创建认证token
            return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(emailId, token);
        }
        throw new BadCredentialsException("Invalid emailId or token for API adapter");
    }

    /**
     * Bearer Token认证 (JWT)
     */
    private Authentication attemptBearerTokenAuthentication(HttpServletRequest request) throws AuthenticationException {
        String header = request.getHeader("Authorization");
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {
            throw new BadCredentialsException("Authorization header is missing or invalid");
        }

        try {
            String token = header.substring(7); // 移除 "Bearer " 前缀
            System.out.println(">>> 解析Bearer Token: " + token.substring(0, Math.min(20, token.length())) + "...");

            // 解析JWT Token
            DecodedJWT jwt = JWT.decode(token);
            String username = jwt.getSubject();

            if (StringUtils.isBlank(username)) {
                throw new BadCredentialsException("Bearer Token认证失败：token中缺少用户信息");
            }

            // 验证用户是否存在
            Boolean isValidUser = userService.getUserAccountInfo(username, null);
            if (isValidUser == null || !isValidUser) {
                System.out.println(">>> 无法获取用户登录信息");
                throw new BadCredentialsException("User not found");
            }

            System.out.println(">>> Bearer Token JWT验证成功，用户: " + username);

            // 创建认证token
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null);
            
            return authToken;
        } catch (Exception e) {
            System.out.println(">>> Bearer Token验证失败: " + e.getMessage());
            throw new BadCredentialsException("Bearer Token verification failed: " + e.getMessage());
        }
    }

    // Getter and Setter methods
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    public void setPermissiveUrl(String... urls) {
        List<RequestMatcher> matchers = new ArrayList<>();
        for (String url : urls) {
            matchers.add(new AntPathRequestMatcher(url));
        }
        this.permissiveRequestMatchers = matchers;
    }
}