package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erros de recursos duplicados, indicando que um recurso
 * com os mesmos atributos já existe.
 * 
 * @author Luan T. Felix
 */
public class DuplicateResourceException extends ApiException {

    /**
     * Construtor para nova instância de exceção de recurso duplicado com uma
     * mensagem.
     * 
     * @param message a mensagem de erro associada à exceção
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 409 Conflict}, indicando que o recurso já existe
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

}
