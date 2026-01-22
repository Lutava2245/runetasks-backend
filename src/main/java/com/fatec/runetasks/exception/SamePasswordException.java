package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

public class SamePasswordException extends ApiException {

    public SamePasswordException() {
        super("Erro: Nova senha é igual a anterior.");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
