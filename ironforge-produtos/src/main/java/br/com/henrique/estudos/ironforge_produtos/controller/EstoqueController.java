package br.com.henrique.estudos.ironforge_produtos.controller;

import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueRequestDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque.EstoqueResponseDTO;
import br.com.henrique.estudos.ironforge_produtos.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_produtos.mapper.EstoqueMapper;
import br.com.henrique.estudos.ironforge_produtos.model.Estoque;
import br.com.henrique.estudos.ironforge_produtos.service.EstoqueService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
@Tag(name = "Estoques", description = "Endpoints para controle e consulta de saldos de estoque dos produtos")
public class EstoqueController implements UriHeaderHelper {

    private final EstoqueService service;
    private final EstoqueMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cadastrar saldo de estoque",
            description = "Cria o registro inicial de estoque vinculado a um produto existente. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estoque criado com sucesso",
                    headers = @Header(name = "Location", description = "URI do estoque criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Regra de negócio ou argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado ou estoque já cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> salvar(@RequestBody @Valid EstoqueRequestDTO dto) {
        Estoque estoque = mapper.toEntity(dto);
        service.salvar(estoque);

        URI url = gerarHeaderLocation(estoque.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar saldo de estoque",
            description = "Atualiza as quantidades disponível e reservada de um registro de estoque. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Registro de estoque ou produto não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizar(
            @Parameter(description = "UUID do registro de estoque", example = "8b2e4c10-9a12-4d3e-8f56-1a2b3c4d5e6f")
            @PathVariable("id") UUID id,
            @RequestBody @Valid EstoqueRequestDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Remover registro de estoque",
            description = "Exclui um registro de estoque pelo seu ID. Requer perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estoque removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Registro de estoque não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "UUID do registro de estoque", example = "8b2e4c10-9a12-4d3e-8f56-1a2b3c4d5e6f")
            @PathVariable("id") UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar estoque por ID",
            description = "Retorna os detalhes do saldo de estoque pelo seu identificador. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Registro de estoque não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<EstoqueResponseDTO> buscarPorId(
            @Parameter(description = "UUID do registro de estoque", example = "8b2e4c10-9a12-4d3e-8f56-1a2b3c4d5e6f")
            @PathVariable("id") UUID id) {
        Estoque estoque = service.buscarPorId(id);
        EstoqueResponseDTO dto = mapper.toResponseDTO(estoque);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/produto/{produtoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICO')")
    @Operation(
            summary = "Buscar estoque por ID do produto",
            description = "Consulta o saldo disponível e reservado a partir do UUID do produto. Requer perfil ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque do produto encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado para o produto informado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<EstoqueResponseDTO> buscarPorProdutoId(
            @Parameter(description = "UUID do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("produtoId") UUID produtoId) {
        Estoque estoque = service.buscarPorProdutoId(produtoId);
        EstoqueResponseDTO dto = mapper.toResponseDTO(estoque);
        return ResponseEntity.ok(dto);
    }
}