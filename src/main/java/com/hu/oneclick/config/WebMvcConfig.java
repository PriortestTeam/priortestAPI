
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
        System.out.println("=== WebMvcConfig: 注册时区拦截器，拦截路径: /api/** ===");
        registry.addInterceptor(timezoneInterceptor)
                .addPathPatterns("/api/**"); // 拦截所有API请求
    }
}
