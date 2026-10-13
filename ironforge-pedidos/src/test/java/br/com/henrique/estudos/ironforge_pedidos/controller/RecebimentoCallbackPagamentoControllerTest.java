package br.com.henrique.estudos.ironforge_pedidos.controller;

import br.com.henrique.estudos.ironforge_pedidos.config.SecurityConfig;
import br.com.henrique.estudos.ironforge_pedidos.service.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecebimentoCallbackPagamentoController.class)
@Import(SecurityConfig.class)
public class RecebimentoCallbackPagamentoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    private static final String API_KEY_HEADER = "apiKey";
    private static final String API_KEY_VALUE = "secreta-api-key-123";

    // ==========================================
    // POST /pedidos/callback-pagamentos
    // ==========================================

    @Test
    @DisplayName("POST /pedidos/callback-pagamentos - Deve atualizar status do pagamento com sucesso e retornar 200 OK")
    void deveAtualizarStatusPagamentoComSucesso() throws Exception {

        UUID id = UUID.randomUUID();
        String chavePagamento = "CHAVE-PAG-123456";
        boolean statusPagamento = true;
        String observacoes = "Pagamento aprovado com sucesso via Webhook";

        doNothing().when(pedidoService).atualizarStatusPagamento(
                eq(id),
                eq(chavePagamento),
                eq(statusPagamento),
                eq(observacoes)
        );

        String jsonBody = """
                {
                    "id": "%s",
                    "chavePagamento": "%s",
                    "status": %b,
                    "observacoes": "%s"
                }
                """.formatted(id, chavePagamento, statusPagamento, observacoes);

        mvc.perform(
                        post("/pedidos/callback-pagamentos")
                                .header(API_KEY_HEADER, API_KEY_VALUE)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isOk());

        verify(pedidoService).atualizarStatusPagamento(
                eq(id),
                eq(chavePagamento),
                eq(statusPagamento),
                eq(observacoes)
        );
    }

    @Test
    @DisplayName("POST /pedidos/callback-pagamentos - Deve retornar 400 Bad Request quando o header 'apiKey' obrigatório não for enviado")
    void deveRetornar400QuandoHeaderApiKeyAusente() throws Exception {

        UUID id = UUID.randomUUID();

        String jsonBody = """
                {
                    "id": "%s",
                    "chavePagamento": "CHAVE-123",
                    "status": true,
                    "observacoes": "Teste sem header"
                }
                """.formatted(id);

        mvc.perform(
                        post("/pedidos/callback-pagamentos")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pedidoService);
    }

    @Test
    @DisplayName("POST /pedidos/callback-pagamentos - Deve retornar 404 Not Found quando o pedido não for encontrado no service")
    void deveRetornar404QuandoPedidoNaoEncontrado() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Pedido não encontrado"))
                .when(pedidoService)
                .atualizarStatusPagamento(
                        any(UUID.class),
                        anyString(),
                        anyBoolean(), // <--- Utilizar anyBoolean() para evitar unboxing de null
                        anyString()
                );

        String jsonBody = """
                {
                    "id": "%s",
                    "chavePagamento": "CHAVE-INEXISTENTE",
                    "status": false,
                    "observacoes": "Pedido não existe no banco"
                }
                """.formatted(id);

        mvc.perform(
                        post("/pedidos/callback-pagamentos")
                                .header(API_KEY_HEADER, API_KEY_VALUE)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isNotFound());

        verify(pedidoService).atualizarStatusPagamento(
                any(UUID.class),
                anyString(),
                anyBoolean(),
                anyString()
        );
    }

    @Test
    @DisplayName("POST /pedidos/callback-pagamentos - Deve retornar 400 Bad Request quando service lançar exceção de regra de negócio")
    void deveRetornar400QuandoServiceLancarIllegalArgumentException() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Chave de pagamento não confere com o pedido"))
                .when(pedidoService)
                .atualizarStatusPagamento(
                        any(UUID.class),
                        anyString(),
                        anyBoolean(), // <--- Utilizar anyBoolean() para evitar unboxing de null
                        anyString()
                );

        String jsonBody = """
                {
                    "id": "%s",
                    "chavePagamento": "CHAVE-INCOMPATIVEL",
                    "status": true,
                    "observacoes": "Falha na validação de negócio"
                }
                """.formatted(id);

        mvc.perform(
                        post("/pedidos/callback-pagamentos")
                                .header(API_KEY_HEADER, API_KEY_VALUE)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isBadRequest());

        verify(pedidoService).atualizarStatusPagamento(
                any(UUID.class),
                anyString(),
                anyBoolean(),
                anyString()
        );
    }
}