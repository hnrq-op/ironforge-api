package br.com.henrique.estudos.ironforge_produtos.controller.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados de entrada para cadastro ou atualização de produto")
public record ProdutoRequestDTO(

        @Schema(description = "Código único de identificação logística (SKU)", example = "SUP-WHEY-900G")
        @NotBlank(message = "O SKU é obrigatório")
        String sku,

        @Schema(description = "Nome comercial do produto", example = "Whey Protein Concentrado 900g")
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
        String nome,

        @Schema(description = "Marca ou fabricante do produto", example = "IronForge Nutrition")
        @NotBlank(message = "A marca é obrigatória")
        @Size(min = 3, max = 100, message = "A marca deve ter entre 3 e 100 caracteres")
        String marca,

        @Schema(description = "Descrição detalhada das características do produto", example = "Suplemento proteico de alta absorção sabor chocolate, 24g de proteína por dose.")
        String descricao,

        @Schema(description = "Categoria do produto no catálogo", example = "SUPLEMENTOS")
        @NotBlank(message = "A categoria é obrigatória")
        @Size(min = 3, max = 100, message = "A categoria deve ter entre 3 e 100 caracteres")
        String categoria,

        @Schema(description = "Preço unitário de venda em reais", example = "149.90")
        @NotNull(message = "O preço é obrigatório")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal preco

) {
}