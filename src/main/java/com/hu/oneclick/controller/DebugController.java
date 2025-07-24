
package com.hu.oneclick.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/test")
    public Map<String, Object> testController() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "DebugController is working on server");
        result.put("timestamp", System.currentTimeMillis());
        result.put("server", "guangzhou");
        return result;
    }

    @PostMapping("/headers")
    public Map<String, Object> debugHeaders(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        
        System.out.println("=== DEBUG: 接收到请求 ===");
        System.out.println("=== 请求URL: " + request.getRequestURI() + " ===");
        System.out.println("=== Controller被正确调用 ===");
        
        // 获取所有请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
            System.out.println("=== Header[" + headerName + "] = " + headerValue + " ===");
        }
        
        // 特别检查时区头
        String timezone = request.getHeader("X-User-Timezone");
        System.out.println("=== 特别检查 X-User-Timezone: " + timezone + " ===");
        
        result.put("receivedHeaders", headers);
        result.put("xUserTimezone", timezone);
        result.put("message", "Headers received successfully from guangzhou server");
        result.put("timestamp", System.currentTimeMillis());
        result.put("server", "guangzhou");
        
        return result;
    }
}
