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
        System.out.println("=== TimezoneInterceptor 开始处理请求 ===");
        System.out.println("=== 请求URL: " + request.getRequestURI() + " ===");
        System.out.println("=== 请求方法: " + request.getMethod() + " ===");
        System.out.println("=== 客户端IP: " + getClientIP(request) + " ===");
        
        // 打印所有请求头
        System.out.println("=== 所有请求头信息 ===");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("=== Header[" + headerName + "] = " + headerValue + " ===");
        }
        
        // 从请求头或参数中获取时区信息
        String timezone = request.getHeader("X-User-Timezone");
        System.out.println("=== X-User-Timezone头值: [" + timezone + "] ===");
        
        if (timezone == null || timezone.isEmpty()) {
            timezone = request.getParameter("timezone");
            System.out.println("=== timezone参数值: [" + timezone + "] ===");
        }

        if (timezone != null && !timezone.isEmpty()) {
            // 存储到ThreadLocal中
            TimezoneContext.setUserTimezone(timezone);
            System.out.println("=== 成功设置时区到ThreadLocal: " + timezone + " ===");
            
            // 验证是否设置成功
            String verifyTimezone = TimezoneContext.getUserTimezone();
            System.out.println("=== 验证ThreadLocal中的时区: " + verifyTimezone + " ===");
        } else {
            System.out.println("=== 警告: 没有找到任何时区信息 ===");
        }
        
        System.out.println("=== TimezoneInterceptor 处理完成 ===");
        return true;
    }
    
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal
        TimezoneContext.clear();
    }
}