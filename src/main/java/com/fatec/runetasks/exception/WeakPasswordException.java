package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

public class WeakPasswordException extends ApiException {

    public WeakPasswordException() {
        super("Erro: A senha está muito fraca.");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
    
}
