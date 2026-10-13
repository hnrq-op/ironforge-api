package br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record EstoqueRequestDTO(

        @NotNull(message = "A quantidade disponível é obrigatória")
        @PositiveOrZero(message = "A quantidade disponível não pode ser negativa")
        Integer quantidadeDisponivel,

        @NotNull(message = "A quantidade reservada é obrigatória")
        @PositiveOrZero(message = "A quantidade reservada não pode ser negativa")
        Integer quantidadeReservada,

        @NotNull(message = "O ID do produto é obrigatório")
        UUID produtoId

) {
}