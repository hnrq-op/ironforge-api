package br.com.henrique.estudos.ironforge_pedidos.publisher.representation;

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