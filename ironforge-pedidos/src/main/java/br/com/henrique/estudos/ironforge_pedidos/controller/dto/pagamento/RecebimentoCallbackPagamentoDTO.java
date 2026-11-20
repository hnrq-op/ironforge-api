package br.com.henrique.estudos.ironforge_pedidos.controller.dto.pagamento;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Payload enviado pelo serviço bancário externo para notificar o resultado do pagamento")
public record RecebimentoCallbackPagamentoDTO(

        @Schema(description = "Identificador único (UUID) do pedido", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID id,

        @Schema(description = "Chave da transação de pagamento vinculada ao pedido", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        String chavePagamento,

        @Schema(description = "Resultado do processamento (true = pago/aprovado, false = recusado/erro)", example = "true")
        boolean status,

        @Schema(description = "Mensagem ou detalhe adicional retornado pelo gateway de pagamento", example = "Pagamento via PIX liquidado com sucesso.")
        String observacoes

) {}