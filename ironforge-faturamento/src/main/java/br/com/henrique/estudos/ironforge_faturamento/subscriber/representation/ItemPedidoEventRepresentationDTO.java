package br.com.henrique.estudos.ironforge_faturamento.subscriber.representation;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoEventRepresentationDTO(
        UUID produtoId,
        String sku,
        String nome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {}