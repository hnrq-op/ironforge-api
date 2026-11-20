package br.com.henrique.estudos.ironforge_pedidos.controller;

import br.com.henrique.estudos.ironforge_pedidos.controller.dto.pagamento.RecebimentoCallbackPagamentoDTO;
import br.com.henrique.estudos.ironforge_pedidos.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos/callback-pagamentos")
@RequiredArgsConstructor
@Tag(name = "Callbacks de Pagamento", description = "Webhook externo para confirmação ou recusa de pagamentos pelo serviço bancário")
public class RecebimentoCallbackPagamentoController {

    private final PedidoService service;

    @PostMapping
    @Operation(
            summary = "Receber atualização de status de pagamento",
            description = "Endpoint acionado pelo serviço bancário externo para confirmar ou recusar o pagamento de um pedido. Autenticado via cabeçalho apiKey.",
            security = @SecurityRequirement(name = "apiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Callback de pagamento processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou incompatíveis com o pedido/chave de pagamento",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Chave de API (apiKey) ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizarStatusPagamento(
            @RequestBody RecebimentoCallbackPagamentoDTO body,
            @Parameter(hidden = true) @RequestHeader(name = "apiKey") String apiKey) {

        service.atualizarStatusPagamento(
                body.id(),
                body.chavePagamento(),
                body.status(),
                body.observacoes()
        );

        return ResponseEntity.ok().build();
    }
}