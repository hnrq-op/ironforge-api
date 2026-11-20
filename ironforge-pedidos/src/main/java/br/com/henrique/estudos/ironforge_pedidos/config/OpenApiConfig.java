package br.com.henrique.estudos.ironforge_pedidos.config;

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
                title = "IronForge - API de Pedidos",
                version = "v1",
                description = "Criação, consulta, atualização de pedidos e recepção de callbacks de pagamento.",
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
@SecurityScheme(
        name = "apiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "apiKey",
        description = "Chave de API utilizada pelo gateway bancário no webhook de pagamentos"
)
public class OpenApiConfig {
}