package br.com.henrique.estudos.ironforge_logistica.controller;

import br.com.henrique.estudos.ironforge_logistica.controller.dto.RecebimentoCallbackLogisticaDTO;
import br.com.henrique.estudos.ironforge_logistica.controller.exception.ErroResposta;
import br.com.henrique.estudos.ironforge_logistica.service.EntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logistica/callback-logistica")
@RequiredArgsConstructor
@Tag(name = "Callbacks de Logística", description = "Webhook externo para atualização de rastreio e status de entrega pelas transportadoras")
public class RecebimentoCallbackLogisticaController {

    private final EntregaService service;

    @PostMapping
    @Operation(
            summary = "Receber atualização de status de entrega",
            description = "Endpoint acionado pela transportadora externa para informar código de rastreio, previsão e evolução do status da entrega. Autenticado via cabeçalho apiKey.",
            security = @SecurityRequirement(name = "apiKeyAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Callback de logística processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "JSON malformado, cabeçalho apiKey ausente ou argumento inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "401", description = "Chave de API (apiKey) ausente ou inválida", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Entrega ou pedido não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados ou regra de negócio",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResposta.class)))
    })
    public ResponseEntity<Void> atualizarStatusEntrega(
            @RequestBody @Valid RecebimentoCallbackLogisticaDTO dto,
            @Parameter(hidden = true) @RequestHeader(name = "apiKey") String apiKey) {
        service.processarCallbackTransportadora(dto);
        return ResponseEntity.ok().build();
    }
}