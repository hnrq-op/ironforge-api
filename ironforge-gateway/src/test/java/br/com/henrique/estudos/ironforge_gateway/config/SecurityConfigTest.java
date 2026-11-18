package br.com.henrique.estudos.ironforge_gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(context)
                .apply(springSecurity())
                .configureClient()
                .build();
    }

    @Test
    @DisplayName("Deve permitir acesso público ao endpoint /actuator/health sem autenticação")
    void devePermitirAcessoPublicoAoActuatorHealth() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Deve permitir leitura pública (GET) no catálogo de produtos sem token JWT")
    void devePermitirLeituraPublicaDeProdutosSemToken() {
        webTestClient.get()
                .uri("/produtos")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotIn(401, 403));
    }

    @Test
    @DisplayName("Deve permitir requisições POST nos webhooks de pagamento e logística sem token JWT")
    void devePermitirWebhooksExternosSemJwt() {
        webTestClient.post()
                .uri("/pedidos/callback-pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotIn(401, 403));

        webTestClient.post()
                .uri("/logistica/callback-logistica")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotIn(401, 403));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao acessar rotas protegidas sem token JWT")
    void deveBloquearRotasProtegidasSemToken() {
        webTestClient.post()
                .uri("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/estoques/produto/123")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/clientes/me")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/enderecos/me")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.post()
                .uri("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Deve liberar acesso às rotas protegidas quando um token JWT válido é informado")
    void devePermitirAcessoARotaProtegidaComJwtValido() {
        webTestClient
                .mutateWith(mockJwt())
                .get()
                .uri("/clientes/me")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotIn(401, 403));
    }
}