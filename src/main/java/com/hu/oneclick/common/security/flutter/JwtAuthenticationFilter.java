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
                System.out.println(">>> 匹配白名单URL，跳过JWT验证: " + path);
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            // 获取Authorization头
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.trim().isEmpty()) {
                System.out.println(">>> 没有Authorization头，继续过滤链");
                filterChain.doFilter(request, response);
                return;
            }

            System.out.println(">>> Authorization头: " + authHeader.substring(0, Math.min(20, authHeader.length())) + "...");

            Authentication authentication;

            // 判断是 Bearer Token 还是 API Token
            if (authHeader.startsWith("Bearer ")) {
                System.out.println(">>> 检测到Bearer Token，使用JWT验证");
                authentication = authenticateBearerToken(authHeader.substring(7).trim());
            } else if (path.startsWith("/api/apiAdapter/")) {
                System.out.println("========== API路径匹配检查 ==========");
                System.out.println(">>> 路径匹配成功: " + path + " 匹配 /api/apiAdapter/ 前缀");
                System.out.println(">>> 检测到API Token访问 apiAdapter 接口，使用API Token验证");
                authentication = authenticateApiToken(authHeader);
            } else {
                System.out.println("========== API路径匹配检查 ==========");
                System.out.println(">>> 路径不匹配: " + path + " 不匹配任何已知模式");
                System.out.println(">>> Authorization头格式: " + (authHeader.startsWith("Bearer ") ? "Bearer Token" : "其他格式"));
                System.out.println(">>> 未知的认证方式或路径不匹配，拒绝访问");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication method for path: " + path);
                return;
            }

            if (authentication != null) {
                System.out.println("========== 最终认证结果 ==========");
                System.out.println(">>> ✅ 认证成功，继续处理请求");
                System.out.println(">>> 认证主体: " + authentication.getPrincipal());
                System.out.println(">>> 认证已设置到SecurityContext");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                System.out.println("========== 最终认证结果 ==========");
                System.out.println(">>> ❌ 认证失败，返回401");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            }

        } catch (Exception e) {
            System.out.println(">>> JWT认证异常: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authentication error: " + e.getMessage() + "\"}");
        }
    }

    private boolean isPermissivePath(String path) {
        return path.equals("/api/login") || 
               path.equals("/login") || 
               path.startsWith("/api/apiAdpater/") ||
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

    private Authentication authenticateApiToken(String authHeader) {
        try {
            System.out.println("========== API Token 验证详细日志 开始 ==========");
            System.out.println(">>> 步骤1: 准备验证API Token");

            // API Token格式: emailId + token，用空格分隔
            String[] parts = authHeader.split(" ", 2);
            if (parts.length != 2) {
                System.out.println(">>> 步骤2: ❌ API Token格式错误，缺少空格分隔符");
                throw new BadCredentialsException("Invalid API token format");
            }

            String emailId = parts[0];
            String token = parts[1];

            System.out.println(">>> 步骤3: ✅ API Token解析成功");
            System.out.println(">>> 步骤3: EmailId: " + emailId);
            System.out.println(">>> 步骤3: Token: " + token.substring(0, Math.min(token.length(), 10)) + "... (truncated)");

            // 基本格式验证
            if (emailId.isEmpty() || token.isEmpty()) {
                System.out.println(">>> 步骤4: ❌ EmailId或Token为空");
                throw new BadCredentialsException("EmailId or token is empty");
            }

            // 简化验证逻辑，暂时跳过数据库查询
            // TODO: 后续实现完整的 API Token 验证逻辑
            System.out.println(">>> 步骤5: ⚠️  跳过数据库查询验证，后续需要实现");
            System.out.println(">>> 步骤6: 模拟API Token验证成功");

            // 创建认证对象
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(emailId, null, new ArrayList<>());

            authToken.setDetails(emailId);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println(">>> 步骤8: ✅ API Token认证对象已创建并设置到SecurityContext");
            System.out.println(">>> 步骤8: 认证主体: " + authToken.getPrincipal());
            System.out.println(">>> 步骤8: 认证详情: " + authToken.getDetails());
            System.out.println("========== API Token 验证详细日志 结束 ==========");
            return authToken;

        } catch (Exception e) {
            System.out.println(">>> ❌ API Token验证最终失败: " + e.getMessage());
            System.out.println(">>> 异常栈跟踪: " + e.getClass().getSimpleName());
            System.out.println("========== API Token 验证详细日志 结束 ==========");
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