package com.revshop.userservice.config;

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
    public OpenAPI userServiceAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:9091");
        server.setDescription("User Service");

        Contact contact = new Contact();
        contact.setName("RevShop Team");
        contact.setEmail("support@revshop.com");

        Info info = new Info()
                .title("User Service API")
                .version("1.0")
                .description("API documentation for RevShop User Service - Handles user authentication, registration, and profile management")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
