package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.AuthService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe o email para iniciar o processo de recuperação de
 * senha de um {@link User}.
 * 
 * @author Luan T. Felix
 * @see AuthService#initiatePasswordReset(ForgotPasswordRequest)
 */
@Data
public class ForgotPasswordRequest {

    /**
     * Email do usuário.
     * <p>
     * Não pode ser nulo ou em branco. Deve ter entre 5 e 100 caracteres.
     * <p>
     */
    @Email
    @Valid
    @NotBlank
    @Size(min = 5, max = 100)
    @Schema(example = "user@runetasks.com")
    private String email;

}
