package com.allanbontempo.pagamentos.exception;

public class ValorPagoInvalidoException extends RuntimeException {
    public ValorPagoInvalidoException(String message) {
        super(message);
    }
}