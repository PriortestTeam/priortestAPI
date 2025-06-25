
package com.hu.oneclick.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OneClick API")
                        .version("1.0")
                        .description("OneClick API Documentation")
                        .contact(new Contact()
                                .name("OneClick Team")
                                .email("support@oneclick.com")));
    }
}
