package br.com.henrique.estudos.ironforge_logistica.controller.exceptions;

public record ErroCampo(
        String campo,
        String erro
) {
}
