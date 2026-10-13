package br.com.henrique.estudos.ironforge_pedidos.controller.exception;

public record ErroCampo(
        String campo,
        String erro
) {
}
