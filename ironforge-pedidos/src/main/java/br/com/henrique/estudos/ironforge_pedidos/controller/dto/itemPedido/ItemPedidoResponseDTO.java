package br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados de retorno de um item pertencente ao pedido")
public record ItemPedidoResponseDTO(

        @Schema(description = "Identificador único do item no pedido", example = "9c1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c")
        UUID id,

        @Schema(description = "Identificador único do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID produtoId,

        @Schema(description = "Quantidade adquirida", example = "2")
        Integer quantidade,

        @Schema(description = "Valor unitário registrado no pedido", example = "149.90")
        BigDecimal valorUnitario

) {}