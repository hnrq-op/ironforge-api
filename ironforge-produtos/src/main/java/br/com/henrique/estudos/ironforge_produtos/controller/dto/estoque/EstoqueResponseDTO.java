package br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EstoqueResponseDTO(
        UUID id,
        Integer quantidadeDisponivel,
        Integer quantidadeReservada,
        OffsetDateTime dataAtualizacao,
        UUID produtoId
) {
}