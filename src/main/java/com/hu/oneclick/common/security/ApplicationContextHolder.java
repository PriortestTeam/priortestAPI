
package com.hu.oneclick.common.security;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext持有者，用于在非Spring管理的类中获取Bean
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
        System.out.println(">>> ApplicationContextHolder 初始化完成");
    }
    
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            System.out.println(">>> 警告: ApplicationContext 尚未初始化");
            return null;
        }
        return applicationContext.getBean(clazz);
    }
    
    public static Object getBean(String name) {
        if (applicationContext == null) {
            System.out.println(">>> 警告: ApplicationContext 尚未初始化");
            return null;
        }
        return applicationContext.getBean(name);
    }
}
