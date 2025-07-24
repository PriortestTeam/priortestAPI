
package com.hu.oneclick.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TimezoneInterceptor timezoneInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("=== WebMvcConfig: 开始注册时区拦截器 ===");
        System.out.println("=== 拦截器对象: " + timezoneInterceptor + " ===");
        System.out.println("=== 拦截器类名: " + timezoneInterceptor.getClass().getName() + " ===");
        
        registry.addInterceptor(timezoneInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**");
        
        System.out.println("=== WebMvcConfig: 时区拦截器注册完成，拦截路径: /** ===");
        System.out.println("=== WebMvcConfig: 排除路径: /static/**, /css/**, /js/**, /images/** ==="); 拦截所有请求，包括/api/**
    }
}
