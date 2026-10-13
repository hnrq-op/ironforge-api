package br.com.henrique.estudos.ironforge_produtos.controller.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoRequestDTO (

        @NotBlank(message = "O SKU é obrigatório")
        String sku,

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
        String nome,

        @NotBlank(message = "A marca é obrigatória")
        @Size(min = 3, max = 100, message = "A marca deve ter entre 3 e 100 caracteres")
        String marca,

        String descricao,

        @NotBlank(message = "A categoria é obrigatória")
        @Size(min = 3, max = 100, message = "A categoria deve ter entre 3 e 100 caracteres")
        String categoria,

        @NotNull(message = "O preço é obrigatório")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal preco

){
}
