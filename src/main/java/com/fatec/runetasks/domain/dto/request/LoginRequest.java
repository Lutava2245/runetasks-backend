package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.AuthService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para a autenticação de um {@link User}.
 * 
 * @author Luan T. Felix
 * @see AuthService#authenticate(LoginRequest)
 */
@Data
public class LoginRequest {

    /**
     * Email ou nickname do usuário.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     */
    @Valid
    @NotBlank
    @Schema(example = "user@runetasks.com")
    private String username;

    /**
     * Senha do usuário.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     */
    @Valid
    @NotBlank
    @Schema(example = "SenhaAtual123")
    private String password;

}
