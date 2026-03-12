package com.revshop.paymentservice.config;

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
    public OpenAPI paymentServiceAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:9095");
        server.setDescription("Payment Service");

        Contact contact = new Contact();
        contact.setName("RevShop Team");

        Info info = new Info()
                .title("Payment Service API")
                .version("1.0")
                .description("API documentation for RevShop Payment Service")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
