package com.hu.oneclick;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.hu.oneclick.config.TimezoneInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author qingyang
 */
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration.class
})
@MapperScan("com.hu.oneclick.**.dao")
@EnableScheduling
@EnableSpringUtil
public class OneClickApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(OneClickApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 应用启动完成，开始验证拦截器注册状态 ===");

        // 验证TimezoneInterceptor是否被正确注册为Bean
        try {
            TimezoneInterceptor interceptor = applicationContext.getBean(TimezoneInterceptor.class);
            System.out.println("=== TimezoneInterceptor Bean注册成功: " + interceptor + " ===");
        } catch (Exception e) {
            System.out.println("=== 错误: TimezoneInterceptor Bean未找到: " + e.getMessage() + " ===");
        }

        // 打印所有与时区相关的Bean
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        System.out.println("=== 检查所有Bean中包含'timezone'或'Timezone'的: ===");
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("timezone")) {
                System.out.println("=== 找到时区相关Bean: " + beanName + " ===");
            }
        }

        System.out.println("=== 拦截器验证完成 ===");
    }
}