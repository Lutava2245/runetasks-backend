package com.fatec.runetasks.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.request.ForgotPasswordRequest;
import com.fatec.runetasks.domain.dto.request.LoginRequest;
import com.fatec.runetasks.domain.dto.request.ResetPasswordRequest;
import com.fatec.runetasks.domain.dto.response.LoginResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.AuthService;
import com.fatec.runetasks.service.impl.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar endpoint de autenticação da entidade {@link User}.
 * 
 * @author Luan T. Felix
 * @see UserDetailsServiceImpl
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/auth")
@Tag(name = "Autenticação", description = "Endpoints para gerenciamento de autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para autenticar um usuário e gerar um token JWT.
     * 
     * @param request DTO contendo o nome de usuário e senha para autenticação.
     * @return um {@link ResponseEntity} contendo o token JWT ou mensagem de erro.
     */
    @PostMapping("login")
    @Operation(summary = "Autenticar usuário", description = "Retorna um novo JWT para login de usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.authenticate(request);
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Endpoint para iniciar o processo de recuperação de senha.
     * 
     * @param request DTO contendo o email do usuário para iniciar o processo de
     *                recuperação de senha.
     * @return um {@link ResponseEntity} com status {@code 202 Accepted} se a
     *         solicitação for aceita ou mensagem de erro.
     */
    @PostMapping("forgot-password")
    @Operation(summary = "Iniciar processo de recuperação de senha", description = "Envia um email com link para redefinir a senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Solicitação aceita"),
    })
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Endpoint para redefinir a senha do usuário.
     * 
     * @param request DTO contendo o token de troca de senha e a nova senha para
     *                redefinir a senha do usuário.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se a
     *         senha for redefinida com sucesso ou mensagem de erro.
     */
    @PostMapping("reset-password")
    @Operation(summary = "Redefinir senha", description = "Redefine a senha do usuário com base em um token de troca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado"),
            @ApiResponse(responseCode = "404", description = "Token não encontrado"),
            @ApiResponse(responseCode = "409", description = "Nova senha é igual à anterior")
    })
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

}
