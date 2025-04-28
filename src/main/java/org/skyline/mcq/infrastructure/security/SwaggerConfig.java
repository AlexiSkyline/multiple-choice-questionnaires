package org.skyline.mcq.infrastructure.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Multiple Choice Questionnaires API")
                        .version("1.0.0")
                        .description("This API provides endpoints for creating, managing, and taking multiple-choice questionnaires and surveys. It includes user authentication, survey management, question management, and result tracking features.")
                        //.termsOfService("https://example.com/terms")
                        .contact(new Contact()
                                .name("API Support")
                                .url("https://github.com/AlexiSkyline")
                        )
                )
                .components(new Components().addSecuritySchemes("BearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
