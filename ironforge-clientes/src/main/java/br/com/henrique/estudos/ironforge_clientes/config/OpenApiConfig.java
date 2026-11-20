package br.com.henrique.estudos.ironforge_clientes.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "IronForge - API de Clientes e Endereços",
                version = "v1",
                description = "Gerenciamento de perfis de clientes, autoatendimento vinculado ao Keycloak e cadastro de endereços de entrega.",
                contact = @Contact(name = "Henrique Oliveira Pires")
        ),
        servers = @Server(url = "/", description = "API Gateway"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Insira o token JWT obtido no Keycloak (sem o prefixo Bearer)"
)
public class OpenApiConfig {
}