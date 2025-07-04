package com.hu.oneclick.common.security;

import com.hu.oneclick.common.security.handler.HttpStatusLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.HttpStatusLogoutSuccessHandler;
import com.hu.oneclick.common.security.handler.JsonLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.JwtRefreshSuccessHandler;
import com.hu.oneclick.common.security.service.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(daoAuthenticationProvider())
            .authenticationProvider(jwtAuthenticationProvider)
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(new AntPathRequestMatcher("/authentication")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/swagger-resources/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/webjars/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/public/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/swagger-ui.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/webjars/**")).permitAll()
                .anyRequest().authenticated()
            )
            .with(new JsonLoginConfigurer<>(), configurer -> configurer.loginSuccessHandler(jsonLoginSuccessHandler))
            .with(new JwtLoginConfigurer<>(), configurer -> configurer.tokenValidSuccessHandler(jwtRefreshSuccessHandler))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(httpStatusLogoutSuccessHandler))
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'")))
            .build();
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

    @Autowired
    public void configureGlobal(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder auth) {
        System.out.println(">>> configureGlobal 注册 provider: " + daoAuthenticationProvider() + ", " + jwtAuthenticationProvider);
        auth.authenticationProvider(daoAuthenticationProvider());
        auth.authenticationProvider(jwtAuthenticationProvider);
    }
}