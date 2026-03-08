package com.fatec.runetasks.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI para a documentação da API.
 * <p>
 * Esta classe é responsável por configurar o OpenAPI para a aplicação,
 * definindo
 * as informações da API, como título, descrição, versão e licença, bem como
 * os esquemas de segurança para autenticação JWT.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Configuration
public class OpenApiConfig {

    /**
     * Cria um esquema de segurança para autenticação JWT.
     * 
     * @return um objeto {@link SecurityScheme} configurado para autenticação JWT
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    /**
     * Configura o OpenAPI para a aplicação, definindo as informações da API e os
     * esquemas de segurança.
     * 
     * @return um objeto {@link OpenAPI} configurado para a aplicação
     */
    @Bean
    OpenAPI runetasksOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Authentication"))
                .components(
                        new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info()
                        .title("Runetasks API")
                        .description("API para gerenciamento de tarefas diárias do Runetasks")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
