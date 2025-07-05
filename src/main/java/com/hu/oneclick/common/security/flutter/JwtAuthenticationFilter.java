package com.hu.oneclick.common.security.flutter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.entity.SysUserToken;
import com.hu.oneclick.server.user.UserService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.entity.SysUser;
/**
 * @author qingyang
 * JWT认证过滤器，支持两种Token认证方式：
 * 1. Bearer Token: 用户登录后生成的JWT token，用于前端应用
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
    @Autowired
    private SysUserDao sysUserDao;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    public JwtAuthenticationFilter() {
        // 设置默认的白名单URL
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/login");
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/register");
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/logout");
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/swagger-ui/**");
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/v3/api-docs/**");
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/swagger-resources/**");
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/webjars/**");
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
        System.out.println(">>> [" + request.getMethod() + "] JwtAuthenticationFilter 收到请求: " + request.getRequestURI();
        String path = request.getRequestURI();
        // 检查是否是需要跳过JWT验证的路径（登录、Swagger等）
        if (isPermissivePath(path) {
            System.out.println(">>> 跳过JWT验证，直接放行请求: " + path);
            filterChain.doFilter(request, response);
            return;
        }
        // 检查是否是白名单URL
        for (RequestMatcher matcher : permissiveRequestMatchers) {
            if (matcher.matches(request) {
                System.out.println(">>> 匹配白名单URL，跳过JWT验证: " + path);
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 检查是否为API适配器路径，如果是则使用API Token验证
        if (request.getRequestURI().contains("/api/apiAdpater/") {
            System.out.println(">>> API适配器路径，开始API Token验证: " + request.getRequestURI();
            try {
                Authentication authResult = attemptApiTokenAuthentication(request);
                if (authResult != null) {
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    System.out.println(">>> API Token验证成功，继续处理请求");
                    if (authenticationSuccessHandler != null) {
                        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
                    }
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    System.out.println(">>> API Token验证失败");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("API Token verification failed");
                    return;
                }
            } catch (Exception e) {
                System.out.println(">>> API Token验证异常: " + e.getMessage();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("API Token verification error: " + e.getMessage();
                return;
            }
        }
        try {
            String authHeader = request.getHeader("Authorization");
            System.out.println(">>> Authorization Header: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length() + "..." : "null");
            if (authHeader == null || authHeader.trim().isEmpty() {
                System.out.println(">>> 缺少Authorization header");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Missing Authorization header\"}");
                return;
            }
            Authentication authentication = null;
            if (authHeader.startsWith("Bearer ") {
                // Bearer Token (JWT)
                String token = authHeader.substring(7).trim();
                authentication = authenticateBearerToken(token);
            } else {
                // API Token (emailId + token)
                authentication = authenticateApiToken(authHeader);
            }
            if (authentication != null) {
                System.out.println(">>> 认证成功，继续处理请求");
                successHandler.onAuthenticationSuccess(request, response, authentication);
                filterChain.doFilter(request, response);
            } else {
                System.out.println(">>> 认证失败");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Authentication failed\"}");
            }
        } catch (Exception e) {
            System.out.println(">>> JWT认证异常: " + e.getMessage();
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authentication error: " + e.getMessage() + "\"}");
        }
    }
    private boolean isPermissivePath(String path) {
        return path.equals("/api/login") || 
               path.equals("/login") || 
               path.startsWith("/api/swagger-ui/") ||
               path.startsWith("/api/v3/api-docs") ||
               path.equals("/api/swagger-ui.html") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html");
    }
    private Authentication authenticateBearerToken(String token) {
        try {
            System.out.println(">>> 开始验证Bearer Token (JWT)");
            // 解析JWT Token
            DecodedJWT jwt = JWT.decode(token);
            String subject = jwt.getSubject();
            System.out.println(">>> JWT Subject: " + subject);
            // 验证Token是否包含必要信息
            if (subject == null || subject.trim().isEmpty() {
                throw new BadCredentialsException("JWT subject is empty");
            }
            // 验证JWT token是否过期
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.before(new Date() {
                throw new BadCredentialsException("JWT token has expired");
            }
            System.out.println(">>> JWT Token验证成功，用户: " + subject);
            // 创建JwtAuthenticationToken对象以匹配JwtRefreshSuccessHandler的期望
            JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
            authToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println(">>> 认证信息已设置到SecurityContext");
            return authToken;
        } catch (Exception e) {
            System.out.println(">>> JWT Token验证失败: " + e.getMessage();
            throw new BadCredentialsException("JWT token verification failed: " + e.getMessage();
        }
    }
    private Authentication attemptApiTokenAuthentication(HttpServletRequest request) throws AuthenticationException {
        try {
            // 从Header中获取Authorization和emailId
            String tokenValue = request.getHeader("Authorization");
            String emailId = request.getHeader("emailId");
            if (tokenValue == null || emailId == null) {
                throw new BadCredentialsException("Missing Authorization header or emailId");
            }
            System.out.println(">>> API Token验证 - EmailId: " + emailId);
            System.out.println(">>> API Token验证 - Token: " + tokenValue);
            // 验证token和emailId不为空
            if (emailId.trim().isEmpty() || tokenValue.trim().isEmpty() {
                throw new BadCredentialsException("EmailId or token is empty");
            }
            // 通过emailId查询用户
            List<SysUser> users = sysUserDao.queryByLikeEmail(emailId);
            if (users.isEmpty() {
                throw new BadCredentialsException("User not found: " + emailId);
            }
            SysUser user = users.get(0);
            System.out.println(">>> 找到用户: " + user.getEmail() + ", ID: " + user.getId();
            // 查询用户的API Token
            List<SysUserToken> userTokens = sysUserTokenDao.selectByUserIdAndToken(user.getId(), tokenValue);
            if (userTokens.isEmpty() {
                throw new BadCredentialsException("Invalid API token for user: " + emailId);
            }
            SysUserToken userToken = userTokens.get(0);
            System.out.println(">>> 找到API Token: " + userToken.getTokenName();
            // 检查token是否过期
            if (userToken.getExpirationTime() != null && userToken.getExpirationTime().before(new Date() {
                throw new BadCredentialsException("API token expired");
            }
            // 检查token状态
            if (userToken.getIsDel() || !userToken.getStatus() {
                throw new BadCredentialsException("API token is disabled or deleted");
            }
            // 检查API调用次数限制
            if (userToken.getApiTimes() != null && userToken.getApiTimes() <= 0) {
                throw new BadCredentialsException("API token usage limit exceeded");
            }
            System.out.println(">>> API Token验证成功，用户: " + emailId);
            // 减少API调用次数（如果有限制）
            if (userToken.getApiTimes() != null && userToken.getApiTimes() > 0) {
                sysUserTokenDao.decreaseApiTimes(userToken.getId();
                System.out.println(">>> API调用次数已减1，剩余: " + (userToken.getApiTimes() - 1);
            }
            // 创建认证对象
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(emailId, null, new ArrayList<>();
            return authToken;
        } catch (Exception e) {
            System.out.println(">>> API Token验证失败: " + e.getMessage();
            throw new BadCredentialsException("API token verification failed: " + e.getMessage();
        }
    }
    private Authentication authenticateApiToken(String authHeader) {
        try {
            System.out.println(">>> 开始验证API Token");
            // API Token格式: emailId + token，用空格分隔
            String[] parts = authHeader.split(" ", 2);
            if (parts.length != 2) {
                throw new BadCredentialsException("Invalid API token format");
            }
            String emailId = parts[0];
            String token = parts[1];
            System.out.println(">>> API Token - EmailId: " + emailId);
            // 简单验证，不查询数据库
            if (emailId.isEmpty() || token.isEmpty() {
                throw new BadCredentialsException("EmailId or token is empty");
            }
            System.out.println(">>> API Token验证成功，用户: " + emailId);
            // 创建简单的认证对象
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(emailId, null, new ArrayList<>();
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println(">>> 认证信息已设置到SecurityContext");
            return authToken;
        } catch (Exception e) {
            System.out.println(">>> API Token验证失败: " + e.getMessage();
            throw new BadCredentialsException("API token verification failed: " + e.getMessage();
        }
    }
    // Setter methods
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }
     public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }
    public void setPermissiveUrl(String... urls) {
        this.permissiveRequestMatchers.clear();
        for (String url : urls) {
            this.permissiveRequestMatchers.add(new AntPathRequestMatcher(url);
        }
    }
}