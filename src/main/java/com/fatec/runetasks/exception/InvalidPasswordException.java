package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erro de senha inválida, indicando que a senha fornecida
 * é inválida.
 * 
 * @author Luan T. Felix
 */
public class InvalidPasswordException extends ApiException {

    /**
     * Construtor para nova instância de exceção de senha inválida com uma
     * mensagem.
     */
    public InvalidPasswordException() {
        super("Erro: Senha atual está incorreta.");
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 400 Bad Request}, indicando que a solicitação de senha é
     *         inválida
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
