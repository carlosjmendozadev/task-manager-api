package com.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local development server");

        Server productionServer = new Server()
                .url("https://task-manager-api-ella.onrender.com")
                .description("Production server (Render)");

        Info info = new Info()
                .title("Task Manager API")
                .description("API RESTful para gerenciamento de tarefas (CRUD completo) construída com Spring Boot.")
                .version("v0.0.1");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
}
