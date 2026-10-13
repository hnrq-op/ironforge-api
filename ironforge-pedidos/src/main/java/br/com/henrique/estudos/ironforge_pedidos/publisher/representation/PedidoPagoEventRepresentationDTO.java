package br.com.henrique.estudos.ironforge_pedidos.publisher.representation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoPagoEventRepresentationDTO(
        UUID pedidoId,
        String status,
        BigDecimal valorTotal,
        String chavePagamento,
        LocalDateTime dataPagamento,
        ClienteEventRepresentationDTO cliente,
        List<ItemPedidoEventRepresentationDTO> itens
) {}