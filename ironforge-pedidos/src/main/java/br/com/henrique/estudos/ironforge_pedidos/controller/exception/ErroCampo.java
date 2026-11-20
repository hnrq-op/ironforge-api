package br.com.henrique.estudos.ironforge_pedidos.controller.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhamento de erro de validação em um campo específico")
public record ErroCampo(

        @Schema(description = "Nome do campo que falhou na validação", example = "preco")
        String campo,

        @Schema(description = "Mensagem descritiva do erro de validação", example = "O preço não pode ser negativo")
        String erro

) {
}