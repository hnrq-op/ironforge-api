package br.com.henrique.estudos.ironforge_produtos.controller;

import br.com.henrique.estudos.ironforge_produtos.config.SecurityConfig;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.mapper.EstoqueMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.service.EstoqueService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EstoqueController.class)
@Import(SecurityConfig.class)
public class EstoqueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EstoqueService service;

    @MockitoBean
    private EstoqueMapper mapper;

    @Test
    @DisplayName("POST - Deve cadastrar um estoque com sucesso e retornar 201 Created")
    void deveSalvarEstoqueComSucesso() throws Exception {
        UUID estoqueId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Produto produto = new Produto();
        produto.setId(produtoId);

        Estoque estoque = new Estoque(estoqueId, 100, 10, OffsetDateTime.now(), produto);

        when(mapper.toEntity(any(EstoqueRequestDTO.class))).thenReturn(estoque);
        doNothing().when(service).salvar(estoque);

        String jsonBody = """
                {
                    "produtoId": "%s",
                    "quantidadeDisponivel": 100,
                    "quantidadeReservada": 10
                }
                """.formatted(produtoId);

        mvc.perform(
                        post("/estoques")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).salvar(estoque);
    }

    @Test
    @DisplayName("POST - Deve retornar status 422 e corpo estruturado quando o payload for inválido no cadastro")
    void deveRetornarErroDeValidacaoAoSalvarComDadosInvalidos() throws Exception {
        String jsonInvalido = "{}";

        mvc.perform(
                        post("/estoques")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonInvalido)
                ).andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação nos campos preenchidos"))
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("POST - Deve retornar 403 Forbidden quando usuário não tiver ROLE_ADMIN")
    void deveRetornar403AoCadastrarEstoqueSemRoleAdmin() throws Exception {
        String jsonBody = """
                {
                    "produtoId": "%s",
                    "quantidadeDisponivel": 150,
                    "quantidadeReservada": 15
                }
                """.formatted(UUID.randomUUID());

        mvc.perform(
                post("/estoques")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OUTRA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("PUT - Deve atualizar um estoque com sucesso e retornar 204 NO CONTENT")
    void deveAtualizarEstoqueComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        doNothing().when(service).atualizar(eq(id), any(EstoqueRequestDTO.class));

        String jsonBody = """
                {
                    "produtoId": "%s",
                    "quantidadeDisponivel": 150,
                    "quantidadeReservada": 15
                }
                """.formatted(produtoId);

        mvc.perform(
                put("/estoques/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNoContent());

        verify(service).atualizar(eq(id), any(EstoqueRequestDTO.class));
    }

    @Test
    @DisplayName("PUT - Deve retornar 404 Not Found ao tentar atualizar estoque inexistente")
    void deveRetornar404AoAtualizarEstoqueInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Estoque não encontrado"))
                .when(service).atualizar(eq(idInexistente), any(EstoqueRequestDTO.class));

        String jsonBody = """
                {
                    "produtoId": "%s",
                    "quantidadeDisponivel": 150,
                    "quantidadeReservada": 15
                }
                """.formatted(UUID.randomUUID());

        mvc.perform(
                put("/estoques/" + idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - Deve deletar um estoque com sucesso e retornar 204 No Content")
    void deveDeletarEstoqueComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deletar(id);

        mvc.perform(
                delete("/estoques/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNoContent());

        verify(service).deletar(id);
    }

    @Test
    @DisplayName("DELETE - Deve retornar 404 Not Found ao tentar deletar estoque inexistente")
    void deveRetornar404AoDeletarEstoqueInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Estoque não encontrado"))
                .when(service).deletar(idInexistente);

        mvc.perform(
                delete("/estoques/" + idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET - Deve buscar estoque por ID e retornar 200 OK")
    void deveBuscarEstoquePorIdComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());

        Estoque estoque = new Estoque(id, 100, 10, OffsetDateTime.now(), produto);
        EstoqueResponseDTO dto = new EstoqueResponseDTO(id, 100, 10, OffsetDateTime.now(), produto.getId());

        when(service.buscarPorId(id)).thenReturn(estoque);
        when(mapper.toResponseDTO(estoque)).thenReturn(dto);

        mvc.perform(
                        get("/estoques/" + id)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.quantidadeDisponivel").value(100))
                .andExpect(jsonPath("$.quantidadeReservada").value(10));
    }

    @Test
    @DisplayName("GET - Deve retornar 404 Not Found quando o estoque não existir pelo ID")
    void deveRetornar404AoBuscarPorIdInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        when(service.buscarPorId(idInexistente)).thenThrow(new EntityNotFoundException("Estoque não encontrado"));

        mvc.perform(
                get("/estoques/" + idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET - Deve buscar estoque por ID do produto e retornar 200 OK")
    void deveBuscarEstoquePorProdutoIdComSucesso() throws Exception {
        UUID produtoId = UUID.randomUUID();
        UUID estoqueId = UUID.randomUUID();

        Produto produto = new Produto();
        produto.setId(produtoId);

        Estoque estoque = new Estoque(estoqueId, 50, 5, OffsetDateTime.now(), produto);
        EstoqueResponseDTO dto = new EstoqueResponseDTO(estoqueId, 50, 5, OffsetDateTime.now(), produtoId);

        when(service.buscarPorProdutoId(produtoId)).thenReturn(estoque);
        when(mapper.toResponseDTO(estoque)).thenReturn(dto);

        mvc.perform(
                        get("/estoques/produto/" + produtoId)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(estoqueId.toString()))
                .andExpect(jsonPath("$.produtoId").value(produtoId.toString()))
                .andExpect(jsonPath("$.quantidadeDisponivel").value(50));
    }

    @Test
    @DisplayName("GET - Deve retornar 404 Not Found quando não existir estoque para o produto ID especificado")
    void deveRetornar404AoBuscarPorProdutoIdInexistente() throws Exception {
        UUID produtoIdInexistente = UUID.randomUUID();

        when(service.buscarPorProdutoId(produtoIdInexistente)).thenThrow(new EntityNotFoundException("Estoque não encontrado para este produto"));

        mvc.perform(
                get("/estoques/produto/" + produtoIdInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNotFound());
    }
}