package br.com.henrique.estudos.ironforge_produtos.controller.dto.produto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProdutoResponseDTO(
        UUID id,
        String sku,
        String nome,
        String marca,
        String descricao,
        String categoria,
        BigDecimal preco,
        boolean ativo,
        OffsetDateTime dataCriacao
) {
}
