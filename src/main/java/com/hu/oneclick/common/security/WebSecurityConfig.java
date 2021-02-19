package com.hu.oneclick.common.security;

import com.hu.oneclick.common.security.flutter.OptionsRequestFilter;
import com.hu.oneclick.common.security.handler.JsonLoginSuccessHandler;
import com.hu.oneclick.common.security.handler.JwtRefreshSuccessHandler;
import com.hu.oneclick.common.security.handler.TokenClearLogoutHandler;
import com.hu.oneclick.common.security.service.JwtAuthenticationProvider;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author qingyang
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${onclick.config.interceptor.enable}")
    private String enable;

    private final JwtUserServiceImpl jwtUserService;

    private final JsonLoginSuccessHandler jsonLoginSuccessHandler;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final JwtRefreshSuccessHandler jwtRefreshSuccessHandler;

    private final TokenClearLogoutHandler tokenClearLogoutHandler;

    public WebSecurityConfig(JwtUserServiceImpl jwtUserService, JsonLoginSuccessHandler jsonLoginSuccessHandler, JwtAuthenticationProvider jwtAuthenticationProvider, JwtRefreshSuccessHandler jwtRefreshSuccessHandler, TokenClearLogoutHandler tokenClearLogoutHandler) {
        this.jwtUserService = jwtUserService;
        this.jsonLoginSuccessHandler = jsonLoginSuccessHandler;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.jwtRefreshSuccessHandler = jwtRefreshSuccessHandler;
        this.tokenClearLogoutHandler = tokenClearLogoutHandler;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //是否开启安全拦截,false 关闭
        if (!Boolean.parseBoolean(enable)) {
            return;
        }
        http.authorizeRequests()
                .antMatchers("/login").anonymous()
                .antMatchers("/user/register").anonymous()
                .antMatchers("/user/sendEmailCode").anonymous()
                .antMatchers("/user/sendEmailRegisterCode").anonymous()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().disable()
                .cors()
                .and()
                .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                new Header("Access-Control-Allow-Origin", "*"),
                new Header("Access-Control-Expose-Headers", "Authorization"))))
                .and()
                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                .apply(new JsonLoginConfigurer<>()).loginSuccessHandler(jsonLoginSuccessHandler)
                .and()
                .apply(new JwtLoginConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler)
                //设置无权限接口
                .permissiveRequestUrls("/login","/user/register","/user/sendEmailCode","/user/sendEmailRegisterCode")
                .and()
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(tokenClearLogoutHandler)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .and()
                .sessionManagement().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider()).authenticationProvider(jwtAuthenticationProvider);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean("daoAuthenticationProvider")
    protected AuthenticationProvider daoAuthenticationProvider() {
        //这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(jwtUserService);
        return daoProvider;
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "HEAD", "OPTION", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
