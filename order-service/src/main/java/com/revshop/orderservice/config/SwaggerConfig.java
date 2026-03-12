package com.revshop.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI orderServiceAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:9094");

        Info info = new Info()
                .title("Order Service API")
                .version("1.0")
                .description("API documentation for RevShop Order Service");

        return new OpenAPI().info(info).servers(List.of(server));
    }
}
