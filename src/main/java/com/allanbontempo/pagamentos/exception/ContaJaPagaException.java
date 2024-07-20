package com.allanbontempo.pagamentos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ContaJaPagaException extends RuntimeException {
    public ContaJaPagaException(String message) {
        super(message);
    }
}
