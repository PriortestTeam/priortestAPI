package com.hu.oneclick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author qingyang
 */
@SpringBootApplication
@MapperScan("com.hu.oneclick.dao")
public class OneClickApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneClickApplication.class, args);
    }
}
