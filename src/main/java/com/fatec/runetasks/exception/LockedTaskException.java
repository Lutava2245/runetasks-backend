package com.fatec.runetasks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de API para erro de tarefa bloqueada, indicando que a tarefa
 * encontra-se bloqueada e não pode ser modificada.
 * 
 * @author Luan T. Felix
 */
public class LockedTaskException extends ApiException {

    /**
     * Construtor para nova instância de exceção de tarefa bloqueada com uma
     * mensagem.
     * 
     * @param message a mensagem de erro associada à exceção
     */
    public LockedTaskException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code 412 Precondition Failed}, indicando que a pré-condição de
     *         tarefa desbloqueada não foi atendida
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.PRECONDITION_FAILED;
    }

}
