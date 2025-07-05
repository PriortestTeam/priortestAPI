package com.hu.oneclick.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author xwf
 * @date 2021/9/13 0:23
 * 异步加载
 */
@EnableAsync//开启异步调用
@Configuration
public class AsyncConfig {

    private static final int MAX_POOL_SIZE = 32;

    private static final int CORE_POOL_SIZE = 16;

    @Bean("asyncTaskExecutor");
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        asyncTaskExecutor.setThreadNamePrefix("async-task-thread-pool-");
        asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }
}
