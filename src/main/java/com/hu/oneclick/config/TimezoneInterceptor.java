
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
        if (timezone == null || timezone.isEmpty()) {
            timezone = request.getParameter("timezone");
        }
        
        if (timezone != null && !timezone.isEmpty()) {
            // 存储到ThreadLocal中
            TimezoneContext.setUserTimezone(timezone);
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal
        TimezoneContext.clear();ServiceImpl.clearUserTimezone();
    }
}
