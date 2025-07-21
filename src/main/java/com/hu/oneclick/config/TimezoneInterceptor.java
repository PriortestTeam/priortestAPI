package com.hu.oneclick.config;

import com.hu.oneclick.common.util.TimezoneContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TimezoneInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头或参数中获取时区信息
        String timezone = request.getHeader("X-User-Timezone");
        System.out.println("=== TimezoneInterceptor: 请求URL: " + request.getRequestURI() + " ===");
        System.out.println("=== TimezoneInterceptor: X-User-Timezone头: " + timezone + " ===");
        
        if (timezone == null || timezone.isEmpty()) {
            timezone = request.getParameter("timezone");
            System.out.println("=== TimezoneInterceptor: timezone参数: " + timezone + " ===");
        }

        if (timezone != null && !timezone.isEmpty()) {
            // 存储到ThreadLocal中
            TimezoneContext.setUserTimezone(timezone);
            System.out.println("=== TimezoneInterceptor: 时区已设置到ThreadLocal: " + timezone + " ===");
        } else {
            System.out.println("=== TimezoneInterceptor: 没有找到时区信息 ===");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal
        TimezoneContext.clear();
    }
}