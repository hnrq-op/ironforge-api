package br.com.henrique.estudos.ironforge_clientes.controller.exception;

public record ErroCampo(
        String campo,
        String erro
) {
}
