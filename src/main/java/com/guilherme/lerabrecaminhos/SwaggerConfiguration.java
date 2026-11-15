package com.guilherme.lerabrecaminhos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


    // springfox (Swagger 2) foi substituído pelo springdoc (OpenAPI 3), compatível com Spring Boot 3+.
    // Documentação em /v3/api-docs e interface em /swagger-ui/index.html.
    @Configuration
    public class SwaggerConfiguration {

        @Bean
        public OpenAPI api() {
            return new OpenAPI().info(metadata());
        }

        private Info metadata () {
            return new Info()
                    .title("API Modelo")
                    .description("API de modelo para novos projetos")
                    .version("1.0");
        }
    }

