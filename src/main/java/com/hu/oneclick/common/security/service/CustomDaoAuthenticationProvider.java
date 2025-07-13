
package com.hu.oneclick.common.security.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义认证提供者，用于区分用户不存在和密码错误
 */
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {
    
    public CustomDaoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        System.out.println(">>> CustomDaoAuthenticationProvider.authenticate - 用户名: " + username);
        
        try {
            // 先尝试加载用户，这样可以区分用户不存在的情况
            UserDetails user = getUserDetailsService().loadUserByUsername(username);
            System.out.println(">>> 用户存在，继续验证密码");
            
            // 用户存在，继续正常的认证流程
            return super.authenticate(authentication);
            
        } catch (UsernameNotFoundException e) {
            System.out.println(">>> CustomDaoAuthenticationProvider 捕获到用户不存在异常，直接抛出");
            // 直接抛出用户不存在异常，不包装成BadCredentialsException
            throw e;
        }
    }
}
