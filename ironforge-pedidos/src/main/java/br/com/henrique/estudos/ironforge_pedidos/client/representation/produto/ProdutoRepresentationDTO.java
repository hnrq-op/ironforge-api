package br.com.henrique.estudos.ironforge_pedidos.client.representation.produto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoRepresentationDTO(
        UUID id,
        String sku,
        String nome,
        String marca,
        String descricao,
        String categoria,
        BigDecimal preco,
        boolean ativo,
        EstoqueRepresentationDTO estoque
) {}