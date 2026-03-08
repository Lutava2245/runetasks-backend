package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erro de senhas iguais, indicando que a nova senha é a
 * mesma que a anterior.
 * 
 * @author Luan T. Felix
 */
public class SamePasswordException extends ApiException {

    /**
     * Construtor para nova instância de exceção de senha igual com uma mensagem.
     */
    public SamePasswordException() {
        super("Erro: Nova senha é igual a anterior.");
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 409 Conflict}, indicando que a nova senha é igual à anterior e
     *         não pode ser usada
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

}
