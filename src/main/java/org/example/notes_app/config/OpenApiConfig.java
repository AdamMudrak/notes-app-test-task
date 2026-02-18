package org.example.notes_app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SERVER_PATH = "http://localhost:8080";
    private static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .addServersItem(new Server().url(SERVER_PATH))
                .components(new Components().addSecuritySchemes(
                        BASIC_AUTH_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME));
    }
}
