package br.com.henrique.estudos.ironforge_produtos.controller.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de retorno do saldo em estoque de um produto")
public record EstoqueResponseDTO(

        @Schema(description = "Identificador único do registro de estoque", example = "8b2e4c10-9a12-4d3e-8f56-1a2b3c4d5e6f")
        UUID id,

        @Schema(description = "Quantidade de unidades disponíveis para venda", example = "150")
        Integer quantidadeDisponivel,

        @Schema(description = "Quantidade de unidades reservadas em pedidos pendentes", example = "10")
        Integer quantidadeReservada,

        @Schema(description = "Data e hora da última atualização do saldo", example = "2026-11-20T14:30:00-03:00")
        OffsetDateTime dataAtualizacao,

        @Schema(description = "Identificador único do produto associado", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID produtoId

) {
}