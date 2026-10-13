package br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoResponseDTO(
        UUID id,
        UUID produtoId,
        Integer quantidade,
        BigDecimal valorUnitario
) {}