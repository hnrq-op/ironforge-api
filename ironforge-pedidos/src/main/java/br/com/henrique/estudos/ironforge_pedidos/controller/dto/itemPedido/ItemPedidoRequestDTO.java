package br.com.henrique.estudos.ironforge_pedidos.controller.dto.itemPedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Item incluído na solicitação de criação ou atualização de pedido")
public record ItemPedidoRequestDTO(

        @Schema(description = "Identificador único (UUID) do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "O ID do produto é obrigatório.")
        UUID produtoId,

        @Schema(description = "Quantidade de unidades do produto", example = "2")
        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @Schema(description = "Valor unitário do produto no momento da compra", example = "149.90")
        @NotNull(message = "O valor unitário é obrigatório.")
        @Positive(message = "O valor unitário deve ser maior que zero.")
        BigDecimal valorUnitario

) {}