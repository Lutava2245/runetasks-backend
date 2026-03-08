package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erro de senha fraca, indicando que a nova senha não
 * atende aos critérios de segurança.
 * 
 * @author Luan T. Felix
 */
public class WeakPasswordException extends ApiException {

    /**
     * Construtor para nova instância de exceção de senha fraca com uma
     * mensagem.
     */
    public WeakPasswordException() {
        super("Erro: A senha está muito fraca.");
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 400 Bad Request}, indicando que a solicitação de senha é
     *         inválida devido à senha fraca
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
