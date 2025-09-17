package com.employeeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                new Info()
                        .title("Employee Service API")
                        .description("Documentation Employee API v1.0")
        )
                .components(new SecurityRequirement().addList());
                //.servers(List.of(new Server().url("http://localhost:8081").description("Employee Service API")));
    }
}
