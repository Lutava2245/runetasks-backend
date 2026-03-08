package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erros de moedas insuficientes, indicando que o usuário
 * não possui moedas suficientes para realizar uma compra ou ação.
 * 
 * @author Luan T. Felix
 */
public class InsufficientCoinsException extends ApiException {

    /**
     * Construtor para nova instância de exceção de moedas insuficientes com uma
     * mensagem.
     */
    public InsufficientCoinsException() {
        super("Erro: Moedas insuficientes para esta compra.");
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 412 Precondition failed}, indicando que a pré-condição de
     *         moedas suficientes não foi atendida
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }

}
