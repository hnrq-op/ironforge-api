package br.com.henrique.estudos.ironforge_pedidos.controller;

import br.com.henrique.estudos.ironforge_pedidos.config.SecurityConfig;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoRequestDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.mapper.PedidoMapper;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.model.enums.StatusPedido;
import br.com.henrique.estudos.ironforge_pedidos.service.PedidoService;
import br.com.henrique.estudos.ironforge_pedidos.validator.PedidoValidator;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PedidoController.class)
@Import(SecurityConfig.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private PedidoValidator validator;

    @MockitoBean
    private PedidoMapper mapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;


    // ==========================================
    // POST /pedidos
    // ==========================================

    @Test
    @DisplayName("POST /pedidos - Deve salvar pedido com sucesso e retornar 201 Created")
    void deveSalvarPedidoComSucesso() throws Exception {

        UUID id = UUID.randomUUID();

        Pedido pedido = new Pedido();
        pedido.setId(id);

        when(mapper.toEntity(any(PedidoRequestDTO.class))).thenReturn(pedido);
        doNothing().when(validator).validar(pedido);
        doNothing().when(pedidoService).salvar(pedido);

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "observacoes": "Entregar à tarde",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 2,
                            "valorUnitario": 75.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        post("/pedidos")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(mapper).toEntity(any(PedidoRequestDTO.class));
        verify(validator).validar(pedido);
        verify(pedidoService).salvar(pedido);
    }


    @Test
    @DisplayName("POST /pedidos - Deve retornar 422 Unprocessable Content quando campos obrigatórios do DTO não forem informados")
    void deveRetornar422QuandoDtoInvalidoNoSalvar() throws Exception {

        String jsonBodyInvalido = "{}";

        mvc.perform(
                        post("/pedidos")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBodyInvalido)
                )
                .andExpect(status().isUnprocessableContent());

        verifyNoInteractions(mapper, validator, pedidoService);
    }


    @Test
    @DisplayName("POST /pedidos - Deve retornar 400 Bad Request quando validador lançar exceção de negócio")
    void deveRetornar400QuandoValidadorLancarExcecao() throws Exception {

        Pedido pedido = new Pedido();

        when(mapper.toEntity(any(PedidoRequestDTO.class)))
                .thenReturn(pedido);

        doThrow(new IllegalArgumentException("Cliente sem saldo ou inválido"))
                .when(validator)
                .validar(pedido);

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 1,
                            "valorUnitario": 100.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        post("/pedidos")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isBadRequest());

        verify(validator).validar(pedido);
        verify(pedidoService, never()).salvar(any());
    }


    @Test
    @DisplayName("POST /pedidos - Deve retornar 403 Forbidden quando usuário não tiver ROLE_CLIENTE")
    void deveRetornar403AoSalvarPedidoComRoleInvalida() throws Exception {

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 1,
                            "valorUnitario": 100.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        post("/pedidos")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(mapper, validator, pedidoService);
    }


    // ==========================================
    // PUT /pedidos/{id}
    // ==========================================

    @Test
    @DisplayName("PUT /pedidos/{id} - Deve atualizar pedido com sucesso com ROLE_CLIENTE e retornar 204 No Content")
    void deveAtualizarPedidoComRoleClienteComSucesso() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "observacoes": "Atualização do pedido",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 3,
                            "valorUnitario": 50.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        put("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isNoContent());

        verify(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));
    }


    @Test
    @DisplayName("PUT /pedidos/{id} - Deve atualizar pedido com sucesso com ROLE_ADMIN e retornar 204 No Content")
    void deveAtualizarPedidoComRoleAdminComSucesso() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 1,
                            "valorUnitario": 100.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        put("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isNoContent());

        verify(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));
    }


    @Test
    @DisplayName("PUT /pedidos/{id} - Deve retornar 422 Unprocessable Content quando DTO for inválido")
    void deveRetornar422QuandoDtoInvalidoNaAtualizacao() throws Exception {

        UUID id = UUID.randomUUID();

        String jsonBodyInvalido = "{}";

        mvc.perform(
                        put("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBodyInvalido)
                )
                .andExpect(status().isUnprocessableContent());

        verifyNoInteractions(pedidoService);
    }


    @Test
    @DisplayName("PUT /pedidos/{id} - Deve retornar 404 Not Found quando pedido a ser atualizado não existir")
    void deveRetornar404AoAtualizarPedidoInexistente() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Pedido não encontrado"))
                .when(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 1,
                            "valorUnitario": 100.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        put("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isNotFound());

        verify(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));
    }


    @Test
    @DisplayName("PUT /pedidos/{id} - Deve retornar 422 Unprocessable Content quando service lançar IllegalArgumentException")
    void deveRetornar422AoAtualizarComDadosInvalidosNoService() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Quantidade de itens inválida"))
                .when(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 1,
                            "valorUnitario": 100.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        put("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isBadRequest());

        verify(pedidoService)
                .atualizar(eq(id), any(PedidoRequestDTO.class));
    }


    @Test
    @DisplayName("PUT /pedidos/{id} - Deve retornar 403 Forbidden para role não autorizada")
    void deveRetornar403AoAtualizarComRoleInvalida() throws Exception {

        UUID id = UUID.randomUUID();

        String jsonBody = """
                {
                    "clienteId": "%s",
                    "itens": [
                        {
                            "produtoId": "%s",
                            "quantidade": 1,
                            "valorUnitario": 100.00
                        }
                    ]
                }
                """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mvc.perform(
                        put("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoService);
    }


    // ==========================================
    // GET /pedidos/{id}
    // ==========================================

    @Test
    @DisplayName("GET /pedidos/{id} - Deve buscar pedido por ID com sucesso e retornar 200 OK")
    void deveBuscarPorIdComSucesso() throws Exception {

        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Pedido pedido = new Pedido();
        pedido.setId(id);

        PedidoResponseDTO responseDTO = new PedidoResponseDTO(
                id,
                StatusPedido.REALIZADO,
                BigDecimal.valueOf(150.00),
                OffsetDateTime.now(),
                clienteId,
                null,
                "CHAVE-123",
                "Sem observações",
                null,
                null,
                Collections.emptyList()
        );

        when(pedidoService.buscarPorId(id))
                .thenReturn(pedido);

        when(mapper.toResponse(pedido))
                .thenReturn(responseDTO);

        mvc.perform(
                        get("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("REALIZADO"))
                .andExpect(jsonPath("$.valorTotal").value(150.00));

        verify(pedidoService).buscarPorId(id);
        verify(mapper).toResponse(pedido);
    }


    @Test
    @DisplayName("GET /pedidos/{id} - Deve retornar 404 Not Found quando pedido não for encontrado")
    void deveRetornar404AoBuscarPorIdInexistente() throws Exception {

        UUID id = UUID.randomUUID();

        when(pedidoService.buscarPorId(id))
                .thenThrow(new EntityNotFoundException("Pedido não encontrado"));

        mvc.perform(
                        get("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                )
                .andExpect(status().isNotFound());

        verify(pedidoService).buscarPorId(id);
    }


    @Test
    @DisplayName("GET /pedidos/{id} - Deve retornar 403 Forbidden para perfil sem autorização")
    void deveRetornar403AoBuscarPorIdComRoleInvalida() throws Exception {

        UUID id = UUID.randomUUID();

        mvc.perform(
                        get("/pedidos/" + id)
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_OUTRA")
                                ))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoService);
    }


    // ==========================================
    // GET /pedidos/{id}/detalhes
    // ==========================================

    @Test
    @DisplayName("GET /pedidos/{id}/detalhes - Deve buscar pedido com dados completos com sucesso e retornar 200 OK")
    void deveBuscarComDadosCompletosComSucesso() throws Exception {

        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Pedido pedido = new Pedido();
        pedido.setId(id);

        PedidoResponseDTO responseDTO = new PedidoResponseDTO(
                id,
                StatusPedido.PAGO,
                BigDecimal.valueOf(300.00),
                OffsetDateTime.now(),
                clienteId,
                null,
                "CHAVE-456",
                "Obs",
                "BR123456789BR",
                "http://nf.com/123.pdf",
                Collections.emptyList()
        );

        when(pedidoService.buscarComDadosCompletos(id))
                .thenReturn(pedido);

        when(mapper.toResponse(pedido))
                .thenReturn(responseDTO);

        mvc.perform(
                        get("/pedidos/" + id + "/detalhes")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PAGO"))
                .andExpect(jsonPath("$.codigoRastreio").value("BR123456789BR"));

        verify(pedidoService).buscarComDadosCompletos(id);
        verify(mapper).toResponse(pedido);
    }


    @Test
    @DisplayName("GET /pedidos/{id}/detalhes - Deve retornar 404 Not Found quando pedido detalhado não for encontrado")
    void deveRetornar404AoBuscarComDadosCompletosParaPedidoInexistente() throws Exception {

        UUID id = UUID.randomUUID();

        when(pedidoService.buscarComDadosCompletos(id))
                .thenThrow(new EntityNotFoundException("Pedido não encontrado"));

        mvc.perform(
                        get("/pedidos/" + id + "/detalhes")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                )
                .andExpect(status().isNotFound());

        verify(pedidoService).buscarComDadosCompletos(id);
    }


    @Test
    @DisplayName("GET /pedidos/{id}/detalhes - Deve retornar 403 Forbidden para perfil sem autorização")
    void deveRetornar403AoBuscarComDadosCompletosComRoleInvalida() throws Exception {

        UUID id = UUID.randomUUID();

        mvc.perform(
                        get("/pedidos/" + id + "/detalhes")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_OUTRA")
                                ))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoService);
    }


    // ==========================================
    // POST /pedidos/{id}/solicitar-novo-pagamento
    // ==========================================

    @Test
    @DisplayName("POST /pedidos/{id}/solicitar-novo-pagamento - Deve solicitar novo pagamento com sucesso e retornar 204 No Content com ROLE_CLIENTE")
    void deveSolicitarNovoPagamentoComSucessoComRoleCliente() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(pedidoService)
                .solicitarNovoPagamento(id);

        mvc.perform(
                        post("/pedidos/" + id + "/solicitar-novo-pagamento")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                )
                .andExpect(status().isNoContent());

        verify(pedidoService).solicitarNovoPagamento(id);
    }


    @Test
    @DisplayName("POST /pedidos/{id}/solicitar-novo-pagamento - Deve solicitar novo pagamento com sucesso e retornar 204 No Content com ROLE_ADMIN")
    void deveSolicitarNovoPagamentoComSucessoComRoleAdmin() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(pedidoService)
                .solicitarNovoPagamento(id);

        mvc.perform(
                        post("/pedidos/" + id + "/solicitar-novo-pagamento")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                )
                .andExpect(status().isNoContent());

        verify(pedidoService).solicitarNovoPagamento(id);
    }


    @Test
    @DisplayName("POST /pedidos/{id}/solicitar-novo-pagamento - Deve retornar 404 Not Found quando pedido não for encontrado")
    void deveRetornar404AoSolicitarNovoPagamentoParaPedidoInexistente() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Pedido não encontrado"))
                .when(pedidoService)
                .solicitarNovoPagamento(id);

        mvc.perform(
                        post("/pedidos/" + id + "/solicitar-novo-pagamento")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                )
                .andExpect(status().isNotFound());

        verify(pedidoService).solicitarNovoPagamento(id);
    }


    @Test
    @DisplayName("POST /pedidos/{id}/solicitar-novo-pagamento - Deve retornar 500 Internal Server Error quando status do pedido não permitir novo pagamento")
    void deveRetornar422AoSolicitarNovoPagamentoParaPedidoComStatusInvalido() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new IllegalStateException("Pedido já se encontra pago"))
                .when(pedidoService)
                .solicitarNovoPagamento(id);

        mvc.perform(
                        post("/pedidos/" + id + "/solicitar-novo-pagamento")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENTE")
                                ))
                )
                .andExpect(status().isInternalServerError());

        verify(pedidoService).solicitarNovoPagamento(id);
    }


    @Test
    @DisplayName("POST /pedidos/{id}/solicitar-novo-pagamento - Deve retornar 403 Forbidden para perfil sem permissão")
    void deveRetornar403AoSolicitarNovoPagamentoComRoleInvalida() throws Exception {

        UUID id = UUID.randomUUID();

        mvc.perform(
                        post("/pedidos/" + id + "/solicitar-novo-pagamento")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_SERVICO")
                                ))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoService);
    }
}