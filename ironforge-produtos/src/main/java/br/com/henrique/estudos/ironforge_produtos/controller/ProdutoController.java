package br.com.henrique.estudos.ironforge_produtos.controller;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.produto.ProdutoResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_produtos.mapper.ProdutoMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Produto;
import br.com.henrique.estudos.ironforge_produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para consulta pública e gerenciamento do catálogo de produtos")
public class ProdutoController implements UriHeaderHelper {

    private final ProdutoService service;
    private final ProdutoMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cadastrar novo produto",
            description = "Cria um novo produto ativo no catálogo. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso",
                    headers = @Header(name = "Location", description = "URI do produto criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Regra de negócio ou argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Produto com SKU duplicado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> salvar(@RequestBody @Valid ProdutoRequestDTO dto) {
        Produto produto = mapper.toEntity(dto);
        produto.setDataCriacao(OffsetDateTime.now());
        service.salvar(produto);

        URI url = gerarHeaderLocation(produto.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar produto existente",
            description = "Atualiza os dados cadastrais de um produto pelo seu ID. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado ou SKU duplicado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizar(
            @Parameter(description = "UUID do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("id") UUID id,
            @RequestBody @Valid ProdutoRequestDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Remover ou inativar produto",
            description = "Remove/inativa um produto do catálogo pelo seu ID. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "UUID do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("id") UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID",
            description = "Consulta os detalhes de um produto específico. Endpoint público.",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "UUID do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("id") UUID id) {
        Produto produto = service.buscarPorId(id);
        ProdutoResponseDTO dto = mapper.toResponseDTO(produto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(
            summary = "Listar produtos ativos",
            description = "Retorna uma lista paginada de produtos ativos, com filtro opcional por categoria. Endpoint público.",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de produtos retornada com sucesso")
    })
    public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutosAtivos(
            @Parameter(description = "Filtro opcional pelo nome da categoria", example = "SUPLEMENTOS")
            @RequestParam(name = "categoria", required = false) String categoria,
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<Produto> pageProdutos = service.buscarAtivosPorCategoria(categoria, pageable);
        Page<ProdutoResponseDTO> pageDto = pageProdutos.map(mapper::toResponseDTO);

        return ResponseEntity.ok(pageDto);
    }
}