package com.exe.whateat.infrastructure.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    private static final String BEARER_NAME = "Bearer JWT";

    @Value("${spring.application.name}")
    private String appName;

    @Value("${whateat.version}")
    private String version;

    @Bean
    public OpenAPI openAPI() {
        final OpenAPI openAPI = new OpenAPI();
        return openAPI
                .addSecurityItem(new SecurityRequirement()
                        .addList(BEARER_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_NAME, new SecurityScheme()
                                .name(BEARER_NAME)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .type(SecurityScheme.Type.HTTP)))
                .info(new Info()
                        .title(appName)
                        .description("APIs documentation for " + appName)
                        .version(version));
    }
}
