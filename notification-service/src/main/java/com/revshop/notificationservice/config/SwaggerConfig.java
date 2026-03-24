package com.revshop.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

//configuration API
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI notificationServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Notification Service API").version("1.0") )
                .servers(List.of(new Server().url("http://localhost:9096") ) ); // swagger
    }
}
