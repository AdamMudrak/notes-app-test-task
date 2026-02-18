package org.example.notes_app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.path}")
    private String serverPath;
    private static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .addServersItem(new Server().url(serverPath))
                .components(new Components().addSecuritySchemes(
                        BASIC_AUTH_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME));
    }
}
