package br.com.henrique.estudos.ironforge_produtos.controller.exception;

public record ErroCampo(
        String campo,
        String erro
) {
}
