package br.com.henrique.estudos.ironforge_logistica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
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
                title = "IronForge - API de Logística",
                version = "v1",
                description = "Recepção de notificações de rastreamento e atualização de status de entrega via webhook.",
                contact = @Contact(name = "Henrique Oliveira Pires")
        ),
        servers = @Server(url = "/", description = "API Gateway"),
        security = @SecurityRequirement(name = "apiKeyAuth")
)
@SecurityScheme(
        name = "apiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "apiKey",
        description = "Chave de API utilizada pela transportadora externa no webhook de logística"
)
public class OpenApiConfig {
}