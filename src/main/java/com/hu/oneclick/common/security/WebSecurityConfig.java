package com.hu.oneclick.common.security;

import com.hu.oneclick.common.security.handler.HttpStatusLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.HttpStatusLogoutSuccessHandler;
import com.hu.oneclick.common.security.handler.JsonLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.JwtRefreshSuccessHandler;
import com.hu.oneclick.common.security.service.JwtAuthenticationProvider;
import com.hu.oneclick.common.security.flutter.MyUsernamePasswordAuthenticationFilter;
import com.hu.oneclick.common.security.flutter.JwtAuthenticationFilter;
import com.hu.oneclick.common.security.handler.HttpStatusLoginFailureHandler;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

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
    private UserDetailsService userDetailsService;

    @Autowired
    private JsonLoginSuccessHandler jsonLoginSuccessHandler;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void checkJwtProvider() {
        System.out.println(">>> JwtAuthenticationProvider 注入结果: " + jwtAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 支持 {bcrypt}、{noop} 等多种格式
        return org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure custom authentication filters
        MyUsernamePasswordAuthenticationFilter jsonAuthFilter = new MyUsernamePasswordAuthenticationFilter();
        jsonAuthFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler);
        jsonAuthFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler());
        jsonAuthFilter.setSessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy());
        jsonAuthFilter.setAuthenticationManager(authenticationManager());
        
        JwtAuthenticationFilter jwtAuthFilter = applicationContext.getBean(JwtAuthenticationFilter.class);
        jwtAuthFilter.setAuthenticationSuccessHandler(jwtRefreshSuccessHandler);
        jwtAuthFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler());
        jwtAuthFilter.setPermissiveUrl("/authentication", "/login");
        jwtAuthFilter.setAuthenticationManager(authenticationManager());

        // @formatter:off
        http
            .addFilterAfter(jsonAuthFilter, LogoutFilter.class)
            .addFilterBefore(jwtAuthFilter, LogoutFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/authentication",
                    "/login",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/api/swagger-ui/**",
                    "/api/v3/api-docs/**",
                    "/api/swagger-resources/**",
                    "/api/webjars/**",
                    "/actuator/**",
                    "/api/auth/**",
                    "/api/public/**",
                    "/api/swagger-ui.html"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(httpStatusLogoutSuccessHandler))
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'")));
        // @formatter:on

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