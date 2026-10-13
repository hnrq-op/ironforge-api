package br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório.")
        UUID produtoId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotNull(message = "O valor unitário é obrigatório.")
        @Positive(message = "O valor unitário deve ser maior que zero.")
        BigDecimal valorUnitario
) {}