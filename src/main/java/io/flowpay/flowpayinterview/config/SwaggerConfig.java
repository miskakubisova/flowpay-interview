package io.flowpay.flowpayinterview.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Configuration class for setting up Swagger using Springdoc OpenAPI.
 * This class customizes the Swagger UI and API documentation for the Flowpay Interview project.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates a custom OpenAPI configuration.
     *
     * @return OpenAPI instance with custom information.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flowpay Interview API")
                        .version("v1")
                        .description("This API handles operations related to companies and their representatives."));
    }

    /**
     * Configures a grouped API for Swagger UI.
     *
     * @return GroupedOpenApi instance for better organization in the Swagger UI.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("flowpay-interview")
                .pathsToMatch("/**")
                .build();
    }
}
