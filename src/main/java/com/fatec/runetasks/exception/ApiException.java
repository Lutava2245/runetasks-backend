package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção abstrata para erros de API, contendo uma mensagem e um status HTTP
 * associado.
 * 
 * @author Luan T. Felix
 */
public abstract class ApiException extends RuntimeException {

    /**
     * Construtor para nova instância de exceção da API com uma mensagem de erro.
     * 
     * @param message a mensagem de erro associada à exceção
     */
    public ApiException(String message) {
        super(message);
    }

    /**
     * Retorna um {@link HttpStatus} representando o erro que será retornado
     * automaticamente pela API.
     * 
     * @return o status HTTP retornado pela API
     */
    public abstract HttpStatus getStatus();

}