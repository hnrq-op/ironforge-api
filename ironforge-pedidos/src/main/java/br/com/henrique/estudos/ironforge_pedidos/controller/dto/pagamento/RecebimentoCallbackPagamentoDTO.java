package br.com.henrique.estudos.ironforge_pedidos.controller.dto.pagamento;

import java.util.UUID;

public record RecebimentoCallbackPagamentoDTO(
        UUID id,
        String chavePagamento,
        boolean status,
        String observacoes
) {
}