package com.hu.oneclick;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author qingyang
 */
@SpringBootApplication(exclude = {
        springfox.documentation.spring.web.SpringfoxWebMvcConfiguration.class,
        springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration.class
})
@MapperScan("com.hu.oneclick.**.dao")
@EnableScheduling
@EnableSpringUtil
public class OneClickApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneClickApplication.class, args);
    }
}