package com.byt3social.authentication.exceptions;

public class FailedToDeliverEmailException extends RuntimeException {
    public FailedToDeliverEmailException() {
        super("Não foi possível enviar o email");
    }
}
