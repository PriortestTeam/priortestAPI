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
import java.util.Collections;
import org.springframework.context.ApplicationContext;
import com.hu.oneclick.common.security.ApplicationContextHolder;

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

    public JwtAuthenticationFilter() {
        // 设置默认的白名单URL
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/login"));
        this.permissiveRequestMatchers.add(new AntPathRequestMatcher("/api/register"));
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

    private static final ThreadLocal<HttpServletRequest> REQUEST_HOLDER = new ThreadLocal<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println(">>> [" + request.getMethod() + "] JwtAuthenticationFilter 收到请求: " + request.getRequestURI());
        String path = request.getRequestURI();
        
        // 将请求存储到ThreadLocal中，供authenticateApiToken使用
        REQUEST_HOLDER.set(request);

        // 检查是否是需要跳过JWT验证的路径（登录、Swagger等）
        if (isPermissivePath(path)) {
            System.out.println(">>> 跳过JWT验证，直接放行请求: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // 检查是否是白名单URL
        for (RequestMatcher matcher : permissiveRequestMatchers) {
            if (matcher.matches(request)) {
                System.out.println(">>> 匹配白名单URL，跳过JWT验证: " + path);
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 检查Authorization头是否存在
        String authHeader = request.getHeader("Authorization");
        
        try {
            System.out.println(">>> ========== 开始请求处理 ==========");
            System.out.println(">>> 请求路径: " + path);
            System.out.println(">>> 请求方法: " + request.getMethod());
            System.out.println(">>> 原始Authorization头: " + authHeader);
            System.out.println(">>> Authorization头长度: " + (authHeader != null ? authHeader.length() : 0));
            System.out.println(">>> Authorization头前缀: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));

            if (authHeader == null || authHeader.trim().isEmpty()) {
                System.out.println(">>> Authorization头为空，返回401");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Authorization header is missing\"}");
                return;
            }

            Authentication authentication = null;

            // 首先尝试JWT验证
            if (authHeader.startsWith("Bearer ")) {
                System.out.println(">>> 检测到Bearer token，尝试JWT验证");
                authentication = authenticateBearerToken(authHeader.substring(7).trim());
            } else {
                System.out.println(">>> 检测到非Bearer token，当作API token处理");
                // 直接将整个authHeader作为API token处理
                authentication = authenticateApiToken(authHeader.trim());
            }

            if (authentication == null) {
                System.out.println(">>> 认证失败，token无效");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }

            System.out.println(">>> 认证成功，继续处理请求");
            System.out.println(">>> 认证对象类型: " + authentication.getClass().getSimpleName());
            System.out.println(">>> 认证用户: " + authentication.getName());
            System.out.println(">>> 即将继续过滤链处理");
            filterChain.doFilter(request, response);
            System.out.println(">>> 过滤链处理完成，响应状态: " + response.getStatus());

        } catch (Exception e) {
            System.out.println(">>> Token验证失败:");
            System.out.println(">>>   - 路径: " + path);
            System.out.println(">>>   - Authorization头格式: " + (authHeader != null && authHeader.length() > 10 ? authHeader.substring(0, 10) + "..." : authHeader));
            System.out.println(">>>   - 错误信息: " + e.getMessage());
            System.out.println(">>> 返回401未授权");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Authentication failed\",\"message\":\"" + e.getMessage() + "\"}");
            return;
        } finally {
            // 清理ThreadLocal
            REQUEST_HOLDER.remove();
        }
    }
    
    private HttpServletRequest getCurrentRequest() {
        return REQUEST_HOLDER.get();
    }

    private boolean isPermissivePath(String path) {
        return path.equals("/api/login") || 
               path.equals("/login") || 
               path.equals("/api/user/register") ||
               path.equals("/api/user/forgetThePassword") ||
               path.equals("/api/user/forgetThePasswordIn") ||
               path.equals("/api/user/activateAccount") ||
               path.equals("/api/user/applyForAnExtension") ||
               path.equals("/api/user/getUserActivNumber") ||
               path.equals("/api/user/applyForAnExtensionIn") ||
               path.startsWith("/api/apiAdapter/") ||
               path.startsWith("/api/swagger-ui/") ||
               path.startsWith("/api/v3/api-docs") ||
               path.equals("/api/swagger-ui.html") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html");
    }

    private boolean shouldSkipAuthentication(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/login") || 
               path.equals("/login") || 
               path.startsWith("/api/apiAdapter/") ||
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
            if (subject == null || subject.trim().isEmpty()) {
                throw new BadCredentialsException("JWT subject is empty");
            }

            // 验证JWT token是否过期
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.before(new Date())) {
                throw new BadCredentialsException("JWT token has expired");
            }

            // ======= 关键：验证JWT token是否为最新有效的token =======
            System.out.println(">>> 开始验证JWT token是否为当前用户的最新有效token");
            String redisKey = "LOGIN_JWT:" + subject;
            
            try {
                // 从Redis获取当前用户的最新token
                org.redisson.api.RBucket<String> bucket = redissonClient.getBucket(redisKey);
                String currentValidToken = bucket.get();
                
                System.out.println(">>> Redis中存储的当前有效token: " + (currentValidToken != null ? currentValidToken.substring(0, Math.min(20, currentValidToken.length())) + "..." : "null"));
                System.out.println(">>> 请求中的token: " + token.substring(0, Math.min(20, token.length())) + "...");
                
                if (currentValidToken == null) {
                    System.out.println(">>> Redis中没有找到用户的有效token，可能已过期或被清除");
                    throw new BadCredentialsException("No valid session found, please login again");
                }
                
                if (!token.equals(currentValidToken)) {
                    System.out.println(">>> Token不匹配！用户可能在其他地方重新登录了");
                    throw new BadCredentialsException("Token has been invalidated by a newer login session");
                }
                
                System.out.println(">>> JWT Token验证通过：token匹配，为当前有效的最新token");
                
            } catch (Exception redisException) {
                System.out.println(">>> Redis验证过程中发生异常: " + redisException.getMessage());
                throw new BadCredentialsException("Session validation failed: " + redisException.getMessage());
            }

            System.out.println(">>> JWT Token验证成功，用户: " + subject);

            // 创建JwtAuthenticationToken对象以匹配JwtRefreshSuccessHandler的期望
            JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
            authToken.setAuthenticated(true);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println(">>> 认证信息已设置到SecurityContext");

            return authToken;

        } catch (Exception e) {
            System.out.println(">>> JWT Token验证失败: " + e.getMessage());
            throw new BadCredentialsException("JWT token verification failed: " + e.getMessage());
        }
    }

    private Authentication authenticateApiToken(String apiToken) {
        try {
            System.out.println(">>> ========== 开始API Token验证 ==========");
            System.out.println(">>> 接收到的API Token: " + apiToken);
            System.out.println(">>> API Token长度: " + apiToken.length());

            // 从request中获取emailId header
            HttpServletRequest currentRequest = getCurrentRequest();
            String emailId = null;
            String token = apiToken.trim();
            
            if (currentRequest != null) {
                emailId = currentRequest.getHeader("emailId");
                System.out.println(">>> 从emailId header获取到: " + emailId);
            }
            
            // 如果没有emailId header，尝试解析原来的格式 (emailId + token用空格分隔)
            if (emailId == null || emailId.trim().isEmpty()) {
                System.out.println(">>> 未找到emailId header，尝试解析空格分隔格式");
                String[] parts = apiToken.split(" ", 2);
                System.out.println(">>> 分割后的部分数量: " + parts.length);

                if (parts.length != 2) {
                    System.out.println(">>> 错误: API Token格式无效，期望: 1) Authorization header + emailId header, 或 2) 'emailId token'格式");
                    throw new BadCredentialsException("Invalid API token format. Expected: Authorization header + emailId header, or 'emailId token' format");
                }

                emailId = parts[0].trim();
                token = parts[1].trim();
            }

            System.out.println(">>> 解析结果 - EmailId: '" + emailId + "'");
            System.out.println(">>> 解析结果 - Token: '" + token.substring(0, Math.min(10, token.length())) + "...'");

            // 基本格式验证
            if (emailId.isEmpty() || token.isEmpty()) {
                System.out.println(">>> 错误: EmailId或Token为空");
                throw new BadCredentialsException("EmailId or token is empty");
            }

            // 实现真正的数据库验证逻辑
            System.out.println(">>> 开始数据库验证流程");

            try {
                // 使用已注入的UserService进行验证
                System.out.println(">>> 调用UserService.getUserAccountInfo进行验证");
                Boolean isValid = userService.getUserAccountInfo(emailId, token);
                
                if (!isValid) {
                    System.out.println(">>> 验证失败: emailId或token无效");
                    throw new BadCredentialsException("Invalid emailId or token");
                }
                
                System.out.println(">>> 数据库验证成功: 用户和token都有效");
                
            } catch (Exception dbException) {
                System.out.println(">>> 数据库验证过程中发生异常: " + dbException.getMessage());
                dbException.printStackTrace();
                throw new BadCredentialsException("Database validation failed: " + dbException.getMessage());
            }

            System.out.println(">>> API Token验证成功，用户: " + emailId);

            // 创建认证对象 - 使用三参数构造函数直接创建已认证的token
            // 三参数构造函数：(principal, credentials, authorities)
            // 这个构造函数会自动将token标记为已认证，无需调用setAuthenticated(true)
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(emailId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println(">>> API Token认证信息已设置到SecurityContext");
            System.out.println(">>> ========== API Token验证完成 ==========");

            return authToken;

        } catch (Exception e) {
            System.out.println(">>> ========== API Token验证失败 ==========");
            System.out.println(">>> 失败原因: " + e.getMessage());
            System.out.println(">>> 异常类型: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.out.println(">>> 根本原因: " + e.getCause().getMessage());
            }
            throw new BadCredentialsException("API token verification failed: " + e.getMessage());
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

    public void setPermissiveUrl(String... urls) {
        this.permissiveRequestMatchers.clear();
        for (String url : urls) {
            this.permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
        }
    }
}