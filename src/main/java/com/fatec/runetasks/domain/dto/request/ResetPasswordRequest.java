package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.PasswordToken;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.AuthService;
import com.fatec.runetasks.util.PasswordValidator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para redefinição de senha de um {@link User}.
 * 
 * @author Luan T. Felix
 * @see AuthService#resetPassword(ResetPasswordRequest)
 */
@Data
public class ResetPasswordRequest {

    /**
     * Token de redefinição de senha.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     * 
     * @see PasswordToken
     */
    @Valid
    @NotBlank
    private String resetToken;

    /**
     * Nova senha do usuário.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     * 
     * @see PasswordValidator#verifyStrength(String)
     */
    @Valid
    @NotBlank
    @Schema(example = "NovaSenha123")
    private String newPassword;

}
