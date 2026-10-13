package br.com.henrique.estudos.ironforge_clientes.controller;

import br.com.henrique.estudos.ironforge_clientes.config.SecurityConfig;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.cliente.ClienteResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.ClienteMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClienteController.class)
@Import(SecurityConfig.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClienteService service;

    @MockitoBean
    private ClienteMapper mapper;

    @Test
    @DisplayName("POST /me - Deve cadastrar o próprio perfil com sucesso e retornar 201 Created")
    void deveCadastrarMeuPerfilComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        String keycloakId = "keycloak-uuid-123";

        Cliente cliente = new Cliente();
        cliente.setId(id);

        when(mapper.toEntity(any(ClienteRequestDTO.class))).thenReturn(cliente);
        doNothing().when(service).salvar(cliente);

        String jsonBody = """
                {
                    "nome": "Cliente Teste",
                    "cpf": "123.456.789-09",
                    "email": "cliente@teste.com",
                    "telefone": "64999999999",
                    "endereco": {
                        "logradouro": "Rua Exemplo",
                        "numero": "100",
                        "bairro": "Centro",
                        "cidade": "Buriti Alegre",
                        "estado": "GO",
                        "cep": "75660000"
                    }
                }
                """;

        mvc.perform(
                        post("/clientes/me")
                                .with(jwt().jwt(builder -> builder.subject(keycloakId)).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).salvar(cliente);
    }

    @Test
    @DisplayName("POST /me - Deve retornar 403 Forbidden quando usuário não tiver ROLE_CLIENTE")
    void deveRetornar403AoCadastrarMeuPerfilSemRoleCliente() throws Exception {
        String jsonBody = """
                {
                    "nome": "Cliente Teste",
                    "cpf": "123.456.789-09",
                    "email": "cliente@teste.com",
                    "telefone": "64999999999",
                    "endereco": {
                        "logradouro": "Rua Exemplo",
                        "numero": "100",
                        "bairro": "Centro",
                        "cidade": "Buriti Alegre",
                        "estado": "GO",
                        "cep": "75660000"
                    }
                }
                """;

        mvc.perform(
                post("/clientes/me")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OUTRA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("GET /me - Deve buscar o próprio perfil com sucesso e retornar 200 OK")
    void deveBuscarMeuPerfilComSucesso() throws Exception {
        String keycloakId = "keycloak-uuid-123";
        Cliente cliente = new Cliente();
        cliente.setKeycloakId(keycloakId);

        ClienteResponseDTO responseDTO = new ClienteResponseDTO(
                UUID.randomUUID(), keycloakId, "Cliente Teste", "123.456.789-09", "cliente@teste.com",
                "64999999999", true, OffsetDateTime.now(), null
        );

        when(service.buscarPorKeycloakId(keycloakId)).thenReturn(cliente);
        when(mapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        mvc.perform(
                        get("/clientes/me")
                                .with(jwt().jwt(builder -> builder.subject(keycloakId)).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.nome").value("Cliente Teste"));

        verify(service).buscarPorKeycloakId(keycloakId);
    }

    @Test
    @DisplayName("PUT /me - Deve atualizar o próprio perfil com sucesso e retornar 204 No Content")
    void deveAtualizarMeuPerfilComSucesso() throws Exception {
        String keycloakId = "keycloak-uuid-123";
        doNothing().when(service).atualizarPorKeycloakId(eq(keycloakId), any(ClienteRequestDTO.class));

        String jsonBody = """
                {
                    "nome": "Cliente Atualizado",
                    "cpf": "123.456.789-09",
                    "email": "cliente@teste.com",
                    "telefone": "64999999999",
                    "endereco": {
                        "logradouro": "Rua Exemplo",
                        "numero": "100",
                        "bairro": "Centro",
                        "cidade": "Buriti Alegre",
                        "estado": "GO",
                        "cep": "75660000"
                    }
                }
                """;

        mvc.perform(
                put("/clientes/me")
                        .with(jwt().jwt(builder -> builder.subject(keycloakId)).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNoContent());

        verify(service).atualizarPorKeycloakId(eq(keycloakId), any(ClienteRequestDTO.class));
    }

    @Test
    @DisplayName("POST / - Deve salvar cliente via Admin com sucesso e retornar 201 Created")
    void deveSalvarClientePorAdminComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(id);

        when(mapper.toEntity(any(ClienteRequestDTO.class))).thenReturn(cliente);
        doNothing().when(service).salvar(cliente);

        String jsonBody = """
                {
                    "nome": "Cliente Admin Novo",
                    "cpf": "123.456.789-09",
                    "email": "cliente@teste.com",
                    "telefone": "64999999999",
                    "endereco": {
                        "logradouro": "Rua Exemplo",
                        "numero": "100",
                        "bairro": "Centro",
                        "cidade": "Buriti Alegre",
                        "estado": "GO",
                        "cep": "75660000"
                    }
                }
                """;

        mvc.perform(
                        post("/clientes")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).salvar(cliente);
    }

    @Test
    @DisplayName("PUT /{id} - Deve atualizar cliente por ID via Admin com sucesso e retornar 204 No Content")
    void deveAtualizarPorIdComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).atualizar(eq(id), any(ClienteRequestDTO.class));

        String jsonBody = """
                {
                    "nome": "Cliente Atualizado",
                    "cpf": "123.456.789-09",
                    "email": "cliente@teste.com",
                    "telefone": "64999999999",
                    "endereco": {
                        "logradouro": "Rua Exemplo",
                        "numero": "100",
                        "bairro": "Centro",
                        "cidade": "Buriti Alegre",
                        "estado": "GO",
                        "cep": "75660000"
                    }
                }
                """;

        mvc.perform(
                put("/clientes/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNoContent());

        verify(service).atualizar(eq(id), any(ClienteRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /{id}/inativar - Deve inativar cliente com sucesso e retornar 204 No Content")
    void deveInativarClienteComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).inativar(id);

        mvc.perform(
                patch("/clientes/" + id + "/inativar")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNoContent());

        verify(service).inativar(id);
    }

    @Test
    @DisplayName("DELETE /{id} - Deve deletar cliente fisicamente com sucesso e retornar 204 No Content")
    void deveDeletarFisicamenteComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deletarFisicamente(id);

        mvc.perform(
                delete("/clientes/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNoContent());

        verify(service).deletarFisicamente(id);
    }


    @Test
    @DisplayName("GET /{id} - Deve buscar por ID com permissão ROLE_SERVICO e retornar 200 OK")
    void deveBuscarPorIdComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(id);

        ClienteResponseDTO responseDTO = new ClienteResponseDTO(
                id, "keycloak-123", "Cliente Teste", "123.456.789-09", "cliente@teste.com",
                "64999999999", true, OffsetDateTime.now(), null
        );

        when(service.buscarPorId(id)).thenReturn(cliente);
        when(mapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        mvc.perform(
                        get("/clientes/" + id)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SERVICO")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(service).buscarPorId(id);
    }

    @Test
    @DisplayName("GET /keycloak/{keycloakId} - Deve buscar por Keycloak ID com sucesso")
    void deveBuscarPorKeycloakIdComSucesso() throws Exception {
        String keycloakId = "keycloak-123";
        Cliente cliente = new Cliente();
        ClienteResponseDTO responseDTO = new ClienteResponseDTO(
                UUID.randomUUID(), keycloakId, "Cliente Teste", "123.456.789-09", "cliente@teste.com",
                "64999999999", true, OffsetDateTime.now(), null
        );

        when(service.buscarPorKeycloakId(keycloakId)).thenReturn(cliente);
        when(mapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        mvc.perform(
                        get("/clientes/keycloak/" + keycloakId)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(keycloakId));

        verify(service).buscarPorKeycloakId(keycloakId);
    }

    @Test
    @DisplayName("GET /cpf/{cpf} - Deve buscar por CPF com sucesso")
    void deveBuscarPorCpfComSucesso() throws Exception {
        String cpf = "123.456.789-09";
        Cliente cliente = new Cliente();
        ClienteResponseDTO responseDTO = new ClienteResponseDTO(
                UUID.randomUUID(), "keycloak-123", "Cliente Teste", cpf, "cliente@teste.com",
                "64999999999", true, OffsetDateTime.now(), null
        );

        when(service.buscarPorCpf(cpf)).thenReturn(cliente);
        when(mapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        mvc.perform(
                        get("/clientes/cpf/" + cpf)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(cpf));

        verify(service).buscarPorCpf(cpf);
    }

    @Test
    @DisplayName("GET /email/{email} - Deve retornar 404 Not Found quando e-mail não for encontrado")
    void deveRetornar404AoBuscarPorEmailInexistente() throws Exception {
        String email = "naoexistente@teste.com";

        when(service.buscarPorEmail(email)).thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        mvc.perform(
                get("/clientes/email/" + email)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SERVICO")))
        ).andExpect(status().isNotFound());

        verify(service).buscarPorEmail(email);
    }
}