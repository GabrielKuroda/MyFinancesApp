package com.dev.myfinance.exceptions;

public class CustomNotFoundException extends RuntimeException {

    public CustomNotFoundException(String entityName, Long id) {
        super(String.format("%s não encontrado(a) com identificador: '%s'", entityName, id));
    }

    public CustomNotFoundException(String entityName) {
        super(String.format("Nenhum objeto encotrado para: %s", entityName));
    }
}