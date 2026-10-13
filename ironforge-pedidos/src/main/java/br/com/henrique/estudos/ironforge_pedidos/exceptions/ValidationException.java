package br.com.henrique.estudos.ironforge_pedidos.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }
}