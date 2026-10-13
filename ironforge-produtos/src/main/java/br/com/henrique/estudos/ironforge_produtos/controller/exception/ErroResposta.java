package br.com.henrique.estudos.ironforge_produtos.controller.exception;

import java.util.List;

public record ErroResposta(
        int status,
        String mensagem,
        List<ErroCampo> erros
) {
}
