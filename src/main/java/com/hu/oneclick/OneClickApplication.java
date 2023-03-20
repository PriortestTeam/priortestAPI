package com.hu.oneclick;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author qingyang
 */
@SpringBootApplication
@MapperScan("com.hu.oneclick.dao")
@EnableScheduling
@EnableOpenApi
public class OneClickApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneClickApplication.class, args);
    }
}
