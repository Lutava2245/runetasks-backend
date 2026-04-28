package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erros de token expirado, indicando que um token de acesso
 * ou recuperação de senha já expirou e não pode mais ser utilizado.
 * 
 * @author Luan T. Felix
 */
public class TokenExpiredException extends ApiException {

    /**
     * Construtor para nova instância de exceção de token expirado com uma mensagem.
     */
    public TokenExpiredException() {
        super("Este link de recuperação expirou. Solicite um novo.");
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 401 Unauthorized}, indicando que o token expirou e não pode
     *         ser utilizado
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
