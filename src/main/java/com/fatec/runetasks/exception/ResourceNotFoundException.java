package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erros de recursos não encontrados, indicando que um
 * recurso com os mesmos atributos não existe.
 * 
 * @author Luan T. Felix
 */
public class ResourceNotFoundException extends ApiException {

    /**
     * Construtor para nova instância de exceção de recurso não encontrado com uma
     * mensagem.
     * 
     * @param message a mensagem de erro associada à exceção
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 404 Not Found}, indicando que o recurso não foi encontrado
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
