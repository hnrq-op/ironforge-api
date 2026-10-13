package br.com.henrique.estudos.ironforge_logistica.controller.exceptions;

import java.util.List;

public record ErroResposta(
        int status,
        String mensagem,
        List<ErroCampo> erros
) {
}
