
package com.hu.oneclick.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 3 Configuration
 *
 * @author xiaohai
 * @date 2023/03/02
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OneClick API Documentation")
                        .version("1.0.0")
                        .description("OneClick项目接口文档"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Authorization"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("emailId"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("user Token"))
                        .addSecuritySchemes("emailId", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("emailId")
                                .description("emailId")));
    }
}
