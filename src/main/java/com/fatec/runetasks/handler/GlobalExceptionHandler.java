package com.fatec.runetasks.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fatec.runetasks.exception.ApiException;

/**
 * Handler global para exceções da aplicação.
 * <p>
 * Esta classe é responsável por centralizar o tratamento de exceções,
 * fornecendo respostas HTTP apropriadas para diferentes tipos de erros.
 * <p>
 * 
 * @author Luan T. Felix
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções do tipo ApiException, retornando o status e a mensagem
     * definidos na exceção.
     * 
     * @param exception exceção do tipo ApiException lançada.
     * @return um {@link ResponseEntity} contendo o status e a mensagem.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(exception.getMessage());
    }

    /**
     * Trata exceções de acesso negado, retornando um status {@code 403 Forbidden} e
     * uma mensagem.
     * 
     * @param exception exceção do tipo AccessDeniedException lançada.
     * @return um {@link ResponseEntity} contendo o status e a mensagem.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso Negado. Você não tem permissão para esta ação.\n"
                        + exception.getMessage());
    }

    /**
     * Trata exceções de método HTTP não suportado, retornando um status
     * {@code 405 Method Not Allowed} e uma mensagem.
     * 
     * @param exception exceção do tipo HttpRequestMethodNotSupportedException
     *                  lançada.
     * @return um {@link ResponseEntity} contendo o status e a mensagem.
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupported(
            org.springframework.web.HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Método HTTP inválido. " + exception.getMessage());
    }

    /**
     * Trata exceções de token JWT expirado, retornando um status
     * {@code 401 Unauthorized} e uma mensagem.
     * 
     * @param exception exceção do tipo ExpiredJwtException lançada.
     * @return um {@link ResponseEntity} contendo o status e a mensagem.
     */
    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(
            io.jsonwebtoken.ExpiredJwtException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Acesso Negado. O Token fornecido já foi expirado, faça o login novamente.\n"
                        + exception.getMessage());
    }

    /**
     * Trata exceções de autenticação, retornando um status {@code 401 Unauthorized} e
     * uma mensagem.
     * 
     * @param exception exceção do tipo AuthenticationException lançada.
     * @return um {@link ResponseEntity} contendo o status e a mensagem.
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<String> handleAuthentication(
            org.springframework.security.core.AuthenticationException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Credenciais inválidas. " + exception.getMessage());
    }

    /**
     * Trata exceções genéricas, retornando um status {@code 500 Internal Server Error} e
     * uma mensagem.
     * 
     * @param exception exceção genérica lançada.
     * @return um {@link ResponseEntity} contendo o status e a mensagem.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception exception) {
        return ResponseEntity
                .internalServerError()
                .body("Ocorreu um erro interno no servidor:\n" + exception.getMessage());
    }

}
