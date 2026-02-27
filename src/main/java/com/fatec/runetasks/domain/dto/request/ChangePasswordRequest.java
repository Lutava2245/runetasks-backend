package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.UserService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para troca de senha de um {@link User}.
 * 
 * @author Luan T. Felix
 * @see UserService#changePassword(Long, ChangePasswordRequest)
 */
@Data
public class ChangePasswordRequest {

    /**
     * Senha atual do usuário.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     */
    @Valid
    @NotBlank
    @Schema(example = "SenhaAtual123")
    private String currentPassword;

    /**
     * Nova senha do usuário.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     * 
     * @see UserService#verifyPasswordStrength(String)
     */
    @Valid
    @NotBlank
    @Schema(example = "NovaSenha123")
    private String newPassword;

}
