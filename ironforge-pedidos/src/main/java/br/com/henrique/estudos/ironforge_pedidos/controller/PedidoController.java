package br.com.henrique.estudos.ironforge_pedidos.controller;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoRequestDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pedido.PedidoResponseDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_pedidos.mapper.PedidoMapper;
import br.com.henrique.estudos.ironforge_pedidos.model.Pedido;
import br.com.henrique.estudos.ironforge_pedidos.service.PedidoService;
import br.com.henrique.estudos.ironforge_pedidos.validator.PedidoValidator;
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
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para criação, consulta, atualização e reprocessamento de pagamento de pedidos")
public class PedidoController implements UriHeaderHelper {

    private final PedidoService pedidoService;
    private final PedidoValidator validator;
    private final PedidoMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Criar novo pedido",
            description = "Valida cliente, produtos e estoque, registra o pedido e inicia a solicitação de pagamento. Requer perfil CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    headers = @Header(name = "Location", description = "URI do pedido criado", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex.: estoque insuficiente ou cliente/produto inativo)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "502", description = "Falha na comunicação com os microsserviços externos (Clientes ou Produtos)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> salvar(@RequestBody @Valid PedidoRequestDTO dto) {
        Pedido pedido = mapper.toEntity(dto);
        validator.validar(pedido);
        pedidoService.salvar(pedido);

        URI url = gerarHeaderLocation(pedido.getId());
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(
            summary = "Atualizar pedido existente",
            description = "Atualiza os itens ou observações de um pedido elegível para alteração. Requer perfil CLIENTE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status do pedido não permite alteração ou regra de negócio inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE ou ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Pedido, cliente ou produto não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "502", description = "Falha na comunicação com os microsserviços externos (Clientes ou Produtos)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizar(
            @Parameter(description = "UUID do pedido", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            @PathVariable("id") UUID id,
            @RequestBody @Valid PedidoRequestDTO dto) {
        pedidoService.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'SERVICO')")
    @Operation(
            summary = "Buscar pedido por ID",
            description = "Retorna os dados básicos e itens de um pedido pelo seu identificador. Requer perfil CLIENTE, ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE, ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "UUID do pedido", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            @PathVariable("id") UUID id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }

    @GetMapping("/{id}/detalhes")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'SERVICO')")
    @Operation(
            summary = "Buscar pedido com dados completos do cliente",
            description = "Consulta o pedido e agrega os dados cadastrais e endereço do cliente via integração interna. Requer perfil CLIENTE, ADMIN ou SERVICO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes completos do pedido retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE, ADMIN ou SERVICO)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Pedido ou cliente não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "502", description = "Falha na comunicação com os microsserviços externos (Clientes ou Produtos)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<PedidoResponseDTO> buscarComDadosCompletos(
            @Parameter(description = "UUID do pedido", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            @PathVariable("id") UUID id) {
        Pedido pedido = pedidoService.buscarComDadosCompletos(id);
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }

    @PostMapping("/{id}/solicitar-novo-pagamento")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(
            summary = "Solicitar nova tentativa de pagamento",
            description = "Gera uma nova chave de pagamento para pedidos cujo pagamento anterior falhou ou expirou. Requer perfil CLIENTE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Nova solicitação de pagamento gerada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status atual do pedido não permite nova solicitação de pagamento",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer role CLIENTE ou ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> solicitarNovoPagamento(
            @Parameter(description = "UUID do pedido", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            @PathVariable("id") UUID id) {
        pedidoService.solicitarNovoPagamento(id);
        return ResponseEntity.noContent().build();
    }
}