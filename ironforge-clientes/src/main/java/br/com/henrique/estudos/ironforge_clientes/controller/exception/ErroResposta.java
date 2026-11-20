package br.com.henrique.estudos.ironforge_clientes.controller.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Estrutura padronizada de resposta de erro da API")
public record ErroResposta(

        @Schema(description = "Código de status HTTP retornado", example = "422")
        int status,

        @Schema(description = "Mensagem resumida descrevendo o erro ocorrido", example = "Erro de validação nos campos preenchidos")
        String mensagem,

        @Schema(description = "Lista de erros específicos por campo (vazia quando não for erro de validação)")
        List<ErroCampo> erros

) {
}