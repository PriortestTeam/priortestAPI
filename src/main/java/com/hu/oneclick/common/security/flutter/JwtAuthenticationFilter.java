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
import java.util.Date;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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
        Authentication authResult = null;
        String authHeader = request.getHeader("Authorization");

        // 根据路径决定认证方式
        if (path.startsWith("/api/apiAdapter/")) {
            // API adapter路径使用API Token认证 (查询数据库)
            System.out.println(">>> API路径，使用API Token认证");
            authResult = attemptApiTokenAuthentication(request);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 其他路径使用JWT Token认证 (不查询数据库)
            System.out.println(">>> 使用Bearer Token认证");
            authResult = attemptBearerTokenAuthentication(request);
        } else {
            System.out.println(">>> 缺少有效的认证信息");
            throw new BadCredentialsException("Missing valid authentication");
        }

        return authResult;
    }

    /**
     * API Token认证 (emailId + token格式) - 仅用于 api/apiAdpater 路径
     */
    private Authentication attemptApiTokenAuthentication(HttpServletRequest request) throws AuthenticationException {
        String header = request.getHeader("Authorization");
        if (StringUtils.isBlank(header)) {
            throw new BadCredentialsException("Missing Authorization header");
        }

        System.out.println(">>> 解析API Token: " + header);

        // API Token格式: emailId + token (空格分隔)
        String[] parts = header.split("\\s+", 2);
        if (parts.length != 2) {
            throw new BadCredentialsException("Invalid API token format. Expected: 'emailId token'");
        }

        String emailId = parts[0];
        String token = parts[1];

        System.out.println(">>> emailId: " + emailId + ", token: " + token.substring(0, Math.min(10, token.length())) + "...");

        // 根据emailId查找用户
        AuthLoginUser user = userService.getUserLoginInfo(emailId);
        if (user == null) {
            System.out.println(">>> 用户不存在: " + emailId);
            throw new BadCredentialsException("User not found: " + emailId);
        }

        // 验证API token - 查询数据库表
        SysUserToken userToken = sysUserTokenDao.findByUserIdAndToken(user.getUserId().toString(), token);
        if (userToken == null) {
            System.out.println(">>> API Token无效或已过期");
            throw new BadCredentialsException("Invalid or expired API token");
        }

        // 检查token是否过期
        if (userToken.getExpirationTime() != null && userToken.getExpirationTime().before(new Date())) {
            System.out.println(">>> API Token已过期");
            throw new BadCredentialsException("API token expired");
        }

        System.out.println(">>> API Token验证成功，用户: " + emailId);

        // 创建认证对象
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        return authToken;
    }

    /**
     * Bearer Token认证 (JWT格式)
     */
    private Authentication attemptBearerTokenAuthentication(HttpServletRequest request) throws AuthenticationException {
        String header = request.getHeader("Authorization");
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid Authorization header format");
        }

        String token = header.substring(7);
        System.out.println(">>> 解析Bearer Token: " + token.substring(0, Math.min(30, token.length())) + "...");

        try {
            // 解析JWT token
            DecodedJWT jwt = JWT.decode(token);
            String username = jwt.getSubject();
            System.out.println(">>> JWT Token解析成功，用户: " + username);

            // JWT token验证不需要查询数据库，直接使用JWT provider验证
            JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt, token);
            return authenticationManager.authenticate(authToken);

        } catch (Exception e) {
            System.out.println(">>> JWT Token验证失败: " + e.getMessage());
            throw new BadCredentialsException("JWT Token verification failed: " + e.getMessage(), e);
        }
    }

    private org.springframework.security.authentication.UsernamePasswordAuthenticationToken authenticateBearerToken(String token) {
        try {
            System.out.println(">>> 开始验证Bearer Token (JWT)");

            // 解析JWT Token
            DecodedJWT jwt = JWT.decode(token);
            String username = jwt.getSubject();

            if (StringUtils.isBlank(username)) {
                throw new BadCredentialsException("Bearer Token认证失败：token中缺少用户信息");
            }

            System.out.println(">>> JWT Token解析成功，用户: " + username);

            // 验证用户是否存在
            Boolean isValidUser = userService.getUserAccountInfo(username, null);
            if (isValidUser == null || !isValidUser) {
                System.out.println(">>> 用户不存在: " + username);
                throw new BadCredentialsException("User not found: " + username);
            }

            // 验证JWT Token的签名和有效期
            // 注意：这里应该使用JWT验证库来验证token的签名
            // 但由于无法获取用户密码作为密钥，暂时只验证基本信息

            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(new Date())) {
                throw new BadCredentialsException("JWT Token已过期");
            }

            System.out.println(">>> JWT Token验证成功，用户: " + username);

            // 创建认证token
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null);

            return authToken;
        } catch (Exception e) {
            System.out.println(">>> JWT Token验证失败: " + e.getMessage());
            throw new BadCredentialsException("JWT Token verification failed: " + e.getMessage());
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