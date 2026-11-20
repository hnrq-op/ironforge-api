package br.com.henrique.estudos.ironforge_produtos.controller.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados completos de um produto do catálogo")
public record ProdutoResponseDTO(

        @Schema(description = "Identificador único do produto", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Código SKU do produto", example = "SUP-WHEY-900G")
        String sku,

        @Schema(description = "Nome comercial do produto", example = "Whey Protein Concentrado 900g")
        String nome,

        @Schema(description = "Marca ou fabricante", example = "IronForge Nutrition")
        String marca,

        @Schema(description = "Descrição detalhada do produto", example = "Suplemento proteico de alta absorção sabor chocolate, 24g de proteína por dose.")
        String descricao,

        @Schema(description = "Categoria do produto", example = "SUPLEMENTOS")
        String categoria,

        @Schema(description = "Preço unitário em reais", example = "149.90")
        BigDecimal preco,

        @Schema(description = "Indica se o produto está ativo e visível para venda", example = "true")
        boolean ativo,

        @Schema(description = "Data e hora de cadastro do produto", example = "2026-11-20T10:15:30-03:00")
        OffsetDateTime dataCriacao

) {
}