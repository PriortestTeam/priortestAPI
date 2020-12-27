package com.hu.oneclick.common.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qingyang
 */
@Configuration
public class MinioConfig {


    @Value("${onclick.minioConfig.endpoint}")
    private String endpoint;

    @Value("${onclick.minioConfig.accountKey}")
    private String accountKey;

    @Value("${onclick.minioConfig.password}")
    private String password;

    @Value("${onclick.minioConfig.source}")
    private String source;

    @Bean
    public MinioClient buildMinIoClientFactory() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(endpoint, accountKey, password,source);
    }

}
