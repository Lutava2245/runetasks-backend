package com.fatec.runetasks.service;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.ForgotPasswordRequest;
import com.fatec.runetasks.domain.dto.request.LoginRequest;
import com.fatec.runetasks.domain.dto.request.ResetPasswordRequest;
import com.fatec.runetasks.domain.dto.response.LoginResponse;
import com.fatec.runetasks.exception.TokenExpiredException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.exception.SamePasswordException;

/**
 * Interface de serviço para operações de autenticação e gerenciamento de senhas
 * de
 * {@link User}.
 * <p>
 * Contém métodos para autenticar usuários, iniciar o processo de redefinição de
 * senha e para redefinir a senha.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface AuthService {

    /**
     * Autentica um usuário com base nas credenciais fornecidas.
     * <p>
     * O processo verifica as credenciais do usuário e, se forem válidas, retorna um
     * token JWT para autenticação futura.
     * <p>
     * 
     * @param request Requisição contendo as credenciais do usuário.
     * @return um {@link LoginResponse} contendo o token JWT.
     * @throws AuthenticationException Caso as credenciais sejam inválidas.
     */
    LoginResponse authenticate(LoginRequest request);

    /**
     * Inicia o processo de recuperação de senha para um usuário.
     * <p>
     * O processo gera um token de troca de senha e envia um email com um link para
     * redefinir a senha do usuário.
     * <p>
     * 
     * @param request Requisição contendo o email do usuário para iniciar o processo
     *                de recuperação de senha.
     */
    void initiatePasswordReset(ForgotPasswordRequest request);

    /**
     * Redefine a senha do usuário.
     * <p>
     * O processo verifica a validade do token e, se for válido, redefine a senha do
     * usuário.
     * <p>
     * 
     * @param request Requisição contendo o token de troca de senha e a nova senha
     *                do usuário.
     * @throws ResourceNotFoundException Caso o token não seja encontrado.
     * @throws TokenExpiredException     Caso o token esteja expirado.
     * @throws SamePasswordException     Caso a nova senha seja igual à anterior.
     */
    void resetPassword(ResetPasswordRequest request);

}
