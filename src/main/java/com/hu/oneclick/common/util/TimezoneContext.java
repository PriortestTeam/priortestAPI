
package com.hu.oneclick.common.util;

/**
 * 时区上下文工具类
 * 用于在当前线程中存储和获取用户时区信息
 */
public class TimezoneContext {
    
    private static final ThreadLocal<String> TIMEZONE_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置当前线程的用户时区
     */
    public static void setUserTimezone(String timezone) {
        TIMEZONE_HOLDER.set(timezone);
    }
    
    /**
     * 获取当前线程的用户时区
     */
    public static String getUserTimezone() {
        return TIMEZONE_HOLDER.get();
    }
    
    /**
     * 清除当前线程的时区信息
     */
    public static void clear() {
        TIMEZONE_HOLDER.remove();
    }
}
