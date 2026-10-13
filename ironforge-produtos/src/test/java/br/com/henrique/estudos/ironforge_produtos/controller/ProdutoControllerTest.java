package br.com.henrique.estudos.ironforge_produtos.controller;

import br.com.henrique.estudos.ironforge_produtos.config.SecurityConfig;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.mapper.ProdutoMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProdutoController.class)
@Import(SecurityConfig.class)
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProdutoService service;

    @MockitoBean
    private ProdutoMapper mapper;

    @Test
    @DisplayName("POST - Deve cadastrar um produto com sucesso e retornar 201 Created")
    void deveSalvarProdutoComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        when(mapper.toEntity(any(ProdutoRequestDTO.class))).thenReturn(produto);
        doNothing().when(service).salvar(any(Produto.class));

        String jsonBody = """
                {
                    "sku": "CREATINA-500G",
                    "nome": "Creatina Monohidratada 500g",
                    "marca": "IronForge",
                    "descricao": "Creatina 100% pura para treino de força",
                    "categoria": "Suplementos",
                    "preco": 119.90
                }
                """;

        mvc.perform(
                        post("/produtos")
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).salvar(any(Produto.class));
    }

    @Test
    @DisplayName("POST - Deve retornar status 422 e corpo estruturado quando o payload for inválido no cadastro")
    void deveRetornarErroDeValidacaoAoSalvarComDadosInvalidos() throws Exception {
        String jsonInvalido = "{}";

        mvc.perform(
                        post("/produtos")
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
    void deveRetornar403AoCadastrarProdutoSemRoleAdmin() throws Exception {
        String jsonBody = """
                {
                    "sku": "CREATINA-500G",
                    "nome": "Creatina Monohidratada 500g",
                    "marca": "IronForge",
                    "descricao": "Creatina 100% pura para treino de força",
                    "categoria": "Suplementos",
                    "preco": 119.90
                }
                """;

        mvc.perform(
                post("/produtos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OUTRA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("PUT - Deve atualizar um produto com sucesso e retornar 204 NO CONTENT")
    void deveAtualizarProdutoComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).atualizar(eq(id), any(ProdutoRequestDTO.class));

        String jsonBody = """
                {
                    "sku": "CREATINA-500G",
                    "nome": "Creatina Monohidratada 500g",
                    "marca": "IronForge",
                    "descricao": "Creatina 100% pura para treino de força",
                    "categoria": "Suplementos",
                    "preco": 119.90
                }
                """;

        mvc.perform(
                put("/produtos/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNoContent());

        verify(service).atualizar(eq(id), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT - Deve retornar 404 Not Found ao tentar atualizar produto inexistente")
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Produto não encontrado"))
                .when(service).atualizar(eq(idInexistente), any(ProdutoRequestDTO.class));

        String jsonBody = """
                {
                    "sku": "CREATINA-500G",
                    "nome": "Creatina Monohidratada 500g",
                    "marca": "IronForge",
                    "descricao": "Creatina 100% pura para treino de força",
                    "categoria": "Suplementos",
                    "preco": 119.90
                }
                """;

        mvc.perform(
                put("/produtos/" + idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - Deve deletar um produto com sucesso e retornar 204 No Content")
    void deveDeletarProdutoComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deletar(id);

        mvc.perform(
                delete("/produtos/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNoContent());

        verify(service).deletar(id);
    }

    @Test
    @DisplayName("DELETE - Deve retornar 404 Not Found ao tentar deletar produto inexistente")
    void deveRetornar404AoDeletarProdutoInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Produto não encontrado"))
                .when(service).deletar(idInexistente);

        mvc.perform(
                delete("/produtos/" + idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET - Deve buscar produto por ID e retornar 200 OK")
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        ProdutoResponseDTO dto = new ProdutoResponseDTO(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        when(service.buscarPorId(id)).thenReturn(produto);
        when(mapper.toResponseDTO(produto)).thenReturn(dto);

        mvc.perform(
                        get("/produtos/" + id)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.sku").value(produto.getSku()));

        verify(service).buscarPorId(id);
    }

    @Test
    @DisplayName("GET - Deve retornar 404 Not Found quando o produto não existir pelo ID")
    void deveRetornar404AoBuscarPorIdInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        when(service.buscarPorId(idInexistente)).thenThrow(new EntityNotFoundException("Produto não encontrado"));

        mvc.perform(
                get("/produtos/" + idInexistente)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET - Deve listar produtos paginados com sucesso sem filtro de categoria")
    void deveListarProdutosPaginadosComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        ProdutoResponseDTO dto = new ProdutoResponseDTO(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", "Suplementos", BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        Page<Produto> pageProduto = new PageImpl<>(List.of(produto), PageRequest.of(0, 10), 1);

        when(service.buscarAtivosPorCategoria(isNull(), any(Pageable.class))).thenReturn(pageProduto);
        when(mapper.toResponseDTO(produto)).thenReturn(dto);

        mvc.perform(
                        get("/produtos")
                                .param("page", "0")
                                .param("size", "10")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].sku").value("CREATINA-500G"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(service).buscarAtivosPorCategoria(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET - Deve listar produtos paginados filtrando por categoria")
    void deveListarProdutosPaginadosFiltrandoPorCategoria() throws Exception {
        String categoria = "Suplementos";
        UUID id = UUID.randomUUID();
        Produto produto = new Produto(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", categoria, BigDecimal.valueOf(119.90), true, OffsetDateTime.now());
        ProdutoResponseDTO dto = new ProdutoResponseDTO(id, "CREATINA-500G", "Creatina Monohidratada 500g", "IronForge", "Creatina 100% pura para treino de força", categoria, BigDecimal.valueOf(119.90), true, OffsetDateTime.now());

        Page<Produto> pageProduto = new PageImpl<>(List.of(produto), PageRequest.of(0, 10), 1);

        when(service.buscarAtivosPorCategoria(eq(categoria), any(Pageable.class))).thenReturn(pageProduto);
        when(mapper.toResponseDTO(produto)).thenReturn(dto);

        mvc.perform(
                        get("/produtos")
                                .param("categoria", categoria)
                                .param("page", "0")
                                .param("size", "10")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].categoria").value(categoria))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service).buscarAtivosPorCategoria(eq(categoria), any(Pageable.class));
    }
}