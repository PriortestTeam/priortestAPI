package com.hu.oneclick.quartz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jenkins");


public class JenkinsConfig {
    private String url;
    private String username;
    private String password;
}
}
