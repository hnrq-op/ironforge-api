package br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

@Schema(description = "Dados de entrada para criação ou atualização de saldo em estoque")
public record EstoqueRequestDTO(

        @Schema(description = "Quantidade de unidades disponíveis para venda", example = "150")
        @NotNull(message = "A quantidade disponível é obrigatória")
        @PositiveOrZero(message = "A quantidade disponível não pode ser negativa")
        Integer quantidadeDisponivel,

        @Schema(description = "Quantidade de unidades reservadas em pedidos pendentes", example = "10")
        @NotNull(message = "A quantidade reservada é obrigatória")
        @PositiveOrZero(message = "A quantidade reservada não pode ser negativa")
        Integer quantidadeReservada,

        @Schema(description = "Identificador único (UUID) do produto vinculado ao estoque", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "O ID do produto é obrigatório")
        UUID produtoId

) {
}