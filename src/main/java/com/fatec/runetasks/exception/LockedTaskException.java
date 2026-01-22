package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

public class LockedTaskException extends ApiException {

    public LockedTaskException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.LOCKED;
    }
    
}
