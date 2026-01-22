package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ApiException {

    public InvalidPasswordException() {
        super("Erro: Senha atual está incorreta.");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
    
}
