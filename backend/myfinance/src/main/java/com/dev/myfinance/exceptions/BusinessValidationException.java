package com.dev.myfinance.exceptions;

import lombok.Getter;

@Getter
public class BusinessValidationException extends RuntimeException {
    private final String field;

    public BusinessValidationException(String field, String message) {
        super(message);
        this.field = field;
    }
}