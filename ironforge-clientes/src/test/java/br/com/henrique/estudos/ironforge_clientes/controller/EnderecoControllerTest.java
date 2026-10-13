package br.com.henrique.estudos.ironforge_clientes.controller;

import br.com.henrique.estudos.ironforge_clientes.config.SecurityConfig;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoRequestDTO;
import br.com.henrique.estudos.ironforge_clientes.controller.dto.endereco.EnderecoResponseDTO;
import br.com.henrique.estudos.ironforge_clientes.mapper.EnderecoMapper;
import br.com.henrique.estudos.ironforge_clientes.model.Cliente;
import br.com.henrique.estudos.ironforge_clientes.model.Endereco;
import br.com.henrique.estudos.ironforge_clientes.service.ClienteService;
import br.com.henrique.estudos.ironforge_clientes.service.EnderecoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EnderecoController.class)
@Import(SecurityConfig.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EnderecoService enderecoService;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private EnderecoMapper mapper;


    @Test
    @DisplayName("POST /enderecos/me - Deve cadastrar o próprio endereço com sucesso e retornar 201 Created")
    void deveCadastrarMeuEnderecoComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        String keycloakId = "keycloak-uuid-123";

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setKeycloakId(keycloakId);

        Endereco endereco = new Endereco();
        endereco.setId(id);

        when(clienteService.buscarPorKeycloakId(keycloakId)).thenReturn(cliente);
        when(mapper.toEntity(any(EnderecoRequestDTO.class))).thenReturn(endereco);
        doNothing().when(enderecoService).salvar(endereco);

        String jsonBody = """
                {
                    "logradouro": "Rua Exemplo",
                    "numero": "100",
                    "complemento": "Apto 101",
                    "bairro": "Centro",
                    "cidade": "Buriti Alegre",
                    "estado": "GO",
                    "cep": "75660000"
                }
                """;

        mvc.perform(
                        post("/enderecos/me")
                                .with(jwt().jwt(builder -> builder.subject(keycloakId)).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(clienteService).buscarPorKeycloakId(keycloakId);
        verify(enderecoService).salvar(endereco);
    }

    @Test
    @DisplayName("GET /enderecos/me - Deve buscar o próprio endereço com sucesso e retornar 200 OK")
    void deveBuscarMeuEnderecoComSucesso() throws Exception {
        String keycloakId = "keycloak-uuid-123";
        UUID clienteId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setKeycloakId(keycloakId);

        Endereco endereco = new Endereco();
        endereco.setId(enderecoId);

        EnderecoResponseDTO responseDTO = new EnderecoResponseDTO(
                enderecoId, "Rua Exemplo", "100", "Apto 101", "Centro", "Buriti Alegre", "GO", "75660000"
        );

        when(clienteService.buscarPorKeycloakId(keycloakId)).thenReturn(cliente);
        when(enderecoService.buscarPorClienteId(clienteId)).thenReturn(endereco);
        when(mapper.toResponseDTO(endereco)).thenReturn(responseDTO);

        mvc.perform(
                        get("/enderecos/me")
                                .with(jwt().jwt(builder -> builder.subject(keycloakId)).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Exemplo"))
                .andExpect(jsonPath("$.cidade").value("Buriti Alegre"));

        verify(clienteService).buscarPorKeycloakId(keycloakId);
        verify(enderecoService).buscarPorClienteId(clienteId);
    }

    @Test
    @DisplayName("PUT /enderecos/me - Deve atualizar o próprio endereço com sucesso e retornar 204 No Content")
    void deveAtualizarMeuEnderecoComSucesso() throws Exception {
        String keycloakId = "keycloak-uuid-123";
        UUID clienteId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setKeycloakId(keycloakId);

        Endereco endereco = new Endereco();
        endereco.setId(enderecoId);

        when(clienteService.buscarPorKeycloakId(keycloakId)).thenReturn(cliente);
        when(enderecoService.buscarPorClienteId(clienteId)).thenReturn(endereco);
        doNothing().when(enderecoService).atualizar(eq(enderecoId), any(EnderecoRequestDTO.class));

        String jsonBody = """
                {
                    "logradouro": "Rua Atualizada",
                    "numero": "200",
                    "bairro": "Centro",
                    "cidade": "Buriti Alegre",
                    "estado": "GO",
                    "cep": "75660000"
                }
                """;

        mvc.perform(
                put("/enderecos/me")
                        .with(jwt().jwt(builder -> builder.subject(keycloakId)).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNoContent());

        verify(clienteService).buscarPorKeycloakId(keycloakId);
        verify(enderecoService).buscarPorClienteId(clienteId);
        verify(enderecoService).atualizar(eq(enderecoId), any(EnderecoRequestDTO.class));
    }


    @Test
    @DisplayName("POST /enderecos/cliente/{clienteId} - Deve salvar endereço por Admin com sucesso e retornar 201 Created")
    void deveSalvarPorAdminComSucesso() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        Endereco endereco = new Endereco();
        endereco.setId(enderecoId);

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(mapper.toEntity(any(EnderecoRequestDTO.class))).thenReturn(endereco);
        doNothing().when(enderecoService).salvar(endereco);

        String jsonBody = """
                {
                    "logradouro": "Rua Admin",
                    "numero": "300",
                    "bairro": "Centro",
                    "cidade": "Buriti Alegre",
                    "estado": "GO",
                    "cep": "75660000"
                }
                """;

        mvc.perform(
                        post("/enderecos/cliente/" + clienteId)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(clienteService).buscarPorId(clienteId);
        verify(enderecoService).salvar(endereco);
    }

    @Test
    @DisplayName("PUT /enderecos/{id} - Deve atualizar endereço por ID via Admin com sucesso e retornar 204 No Content")
    void deveAtualizarPorIdComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(enderecoService).atualizar(eq(id), any(EnderecoRequestDTO.class));

        String jsonBody = """
                {
                    "logradouro": "Rua Atualizada Admin",
                    "numero": "400",
                    "bairro": "Centro",
                    "cidade": "Buriti Alegre",
                    "estado": "GO",
                    "cep": "75660000"
                }
                """;

        mvc.perform(
                put("/enderecos/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNoContent());

        verify(enderecoService).atualizar(eq(id), any(EnderecoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /enderecos/{id} - Deve deletar endereço com sucesso e retornar 204 No Content")
    void deveDeletarComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(enderecoService).deletar(id);

        mvc.perform(
                delete("/enderecos/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNoContent());

        verify(enderecoService).deletar(id);
    }


    @Test
    @DisplayName("GET /enderecos/{id} - Deve buscar por ID com permissão ROLE_SERVICO e retornar 200 OK")
    void deveBuscarPorIdComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Endereco endereco = new Endereco();
        endereco.setId(id);

        EnderecoResponseDTO responseDTO = new EnderecoResponseDTO(
                id, "Rua Exemplo", "100", null, "Centro", "Buriti Alegre", "GO", "75660000"
        );

        when(enderecoService.buscarPorId(id)).thenReturn(endereco);
        when(mapper.toResponseDTO(endereco)).thenReturn(responseDTO);

        mvc.perform(
                        get("/enderecos/" + id)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SERVICO")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(enderecoService).buscarPorId(id);
    }

    @Test
    @DisplayName("GET /enderecos/cliente/{clienteId} - Deve buscar por Cliente ID com sucesso e retornar 200 OK")
    void deveBuscarPorClienteIdComSucesso() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        Endereco endereco = new Endereco();
        endereco.setId(enderecoId);

        EnderecoResponseDTO responseDTO = new EnderecoResponseDTO(
                enderecoId, "Rua Exemplo", "100", null, "Centro", "Buriti Alegre", "GO", "75660000"
        );

        when(enderecoService.buscarPorClienteId(clienteId)).thenReturn(endereco);
        when(mapper.toResponseDTO(endereco)).thenReturn(responseDTO);

        mvc.perform(
                        get("/enderecos/cliente/" + clienteId)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Exemplo"));

        verify(enderecoService).buscarPorClienteId(clienteId);
    }
}