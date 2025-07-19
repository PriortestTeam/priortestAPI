package com.hu.oneclick.common.security;

import com.hu.oneclick.common.security.handler.HttpStatusLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.HttpStatusLogoutSuccessHandler;
import com.hu.oneclick.common.security.handler.JsonLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.JwtRefreshSuccessHandler;
import com.hu.oneclick.common.security.service.JwtAuthenticationProvider;
import com.hu.oneclick.common.security.flutter.MyUsernamePasswordAuthenticationFilter;
import com.hu.oneclick.common.security.flutter.JwtAuthenticationFilter;
import com.hu.oneclick.common.security.handler.HttpStatusLoginFailureHandler;
import com.hu.oneclick.common.security.handler.TokenClearLogoutHandler;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.server.user.UserService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import com.hu.oneclick.common.security.service.JwtAuthenticationProvider;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.CustomDaoAuthenticationProvider;

/**
 * @author qingyang
 */
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private JwtRefreshSuccessHandler jwtRefreshSuccessHandler;

    @Autowired
    private HttpStatusLoginSuccessHandler httpStatusLoginSuccessHandler;

    @Autowired
    private HttpStatusLogoutSuccessHandler httpStatusLogoutSuccessHandler;

    @Autowired
    private TokenClearLogoutHandler tokenClearLogoutHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JsonLoginSuccessHandler jsonLoginSuccessHandler;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Autowired
    private RedissonClient redisClient;

    @PostConstruct
    public void checkJwtProvider() {
        System.out.println(">>> JwtAuthenticationProvider 注入结果: " + jwtAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用DelegatingPasswordEncoder，保持{bcrypt}前缀格式一致
        return new DelegatingPasswordEncoder("bcrypt", 
            Map.of(
                "bcrypt", new BCryptPasswordEncoder(),
                "noop", NoOpPasswordEncoder.getInstance()
            )
        );
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        CustomDaoAuthenticationProvider provider = new CustomDaoAuthenticationProvider(userDetailsService, passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(
            daoAuthenticationProvider(),
            jwtAuthenticationProvider
        ));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(jwtRefreshSuccessHandler);
        filter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler());
        // 不在这里设置白名单，而是在请求匹配器中处理
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println(">>> 配置SecurityFilterChain");

        // Configure custom authentication filters
        MyUsernamePasswordAuthenticationFilter jsonAuthFilter = new MyUsernamePasswordAuthenticationFilter();
        jsonAuthFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler);
        jsonAuthFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler());
        jsonAuthFilter.setSessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy());
        jsonAuthFilter.setAuthenticationManager(authenticationManager());

        // Get the JWT filter from the Spring context
        JwtAuthenticationFilter jwtAuthFilter = jwtAuthenticationFilter();

        // @formatter:off
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/login",
                    "/register", 
                    "/user/register",
                    "/user/forgetThePassword",
                    "/user/forgetThePasswordIn",
                    "/user/activateAccount",
                    "/user/applyForAnExtension",
                    "/user/getUserActivNumber",
                    "/user/applyForAnExtensionIn",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/auth/**",
                    "/public/**",
                    "/actuator/**",
                    "/static/**",
                    "/*.html",
                    "/**/*.html",
                    "/*.js",
                    "/**/*.js",
                    "/*.css",
                    "/**/*.css"
                ).permitAll()
                .requestMatchers("/api/versionQualityReport/**").permitAll()
                .requestMatchers("/version-quality-report.html", "/version-quality-report.js").permitAll()
                .requestMatchers("/version-mapping.html", "/version-mapping.js").permitAll()
                .anyRequest().authenticated()
            )
            // 先添加登录过滤器
            .addFilterBefore(jsonAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // 添加自定义过滤器来处理JWT认证，但排除白名单路径
            .addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    String path = request.getRequestURI();
                    String method = request.getMethod();
                    String authHeader = request.getHeader("Authorization");
                    String contentType = request.getContentType();

                    System.out.println(">>> ========== WebSecurityConfig 过滤器 ==========");
                    System.out.println(">>> 请求路径: " + path);
                    System.out.println(">>> 请求方法: " + method);
                    System.out.println(">>> Content-Type: " + contentType);
                    System.out.println(">>> Authorization头: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
                    System.out.println(">>> 请求参数:");
                    request.getParameterMap().forEach((key, values) -> {
                        System.out.println(">>>   " + key + " = " + String.join(", ", values));
                    });

                    // 如果是白名单路径，完全跳过JWT验证
                    if (path.equals("/api/login") || path.equals("/login") || 
                        path.equals("/api/user/register") ||
                        path.equals("/api/user/forgetThePassword") ||
                        path.equals("/api/user/forgetThePasswordIn") ||
                        path.equals("/api/user/activateAccount") ||
                        path.equals("/api/user/applyForAnExtension") ||
                        path.equals("/api/user/getUserActivNumber") ||
                        path.equals("/api/user/applyForAnExtensionIn") ||
                        path.startsWith("/api/swagger-ui/") ||
                        path.startsWith("/api/v3/api-docs") ||
                        path.equals("/api/swagger-ui.html") ||
                        path.startsWith("/swagger-ui/") ||
                        path.startsWith("/v3/api-docs") ||
                        path.equals("/swagger-ui.html") ||
                        path.endsWith(".html") ||
                        path.endsWith(".js") ||
                        path.endsWith(".css") ||
                        path.startsWith("/api/static/")) {
                        System.out.println(">>> 完全跳过JWT验证，直接放行请求: " + path);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    System.out.println(">>> 需要JWT验证，使用JWT过滤器: " + path);
                    // 对于其他路径，使用JWT过滤器处理
                    jwtAuthFilter.doFilter(request, response, filterChain);
                }
            }, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                    .addLogoutHandler(tokenClearLogoutHandler)
                    .logoutSuccessHandler(httpStatusLogoutSuccessHandler)
                    .permitAll()
                )
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'")));
        // @formatter:on

        System.out.println(">>> SecurityFilterChain配置完成");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}