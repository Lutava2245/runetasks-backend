package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.UserService;
import com.fatec.runetasks.util.PasswordValidator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para registro de um {@link User} no sistema.
 * 
 * @author Luan T. Felix
 * @see UserService#createUser(UserCreateRequest)
 */
@Data
public class UserCreateRequest {

    /**
     * Nome do usuário.
     * <p>
     * Não pode ser nulo ou em branco. Deve ter entre 2 e 100 caracteres.
     * <p>
     */
    @Valid
    @NotBlank
    @Size(min = 2, max = 100)
    @Schema(example = "Usuário RuneTasks")
    private String name;

    /**
     * Nickname do usuário.
     * <p>
     * Não pode ser nulo ou em branco. Deve ter entre 2 e 30 caracteres.
     * <p>
     */
    @Valid
    @NotBlank
    @Size(min = 2, max = 30)
    @Schema(example = "user_runetasks")
    private String nickname;

    /**
     * Senha do usuário.
     * <p>
     * Não pode ser nulo ou em branco. Deve ter no mínimo 8 caracteres.
     * <p>
     * 
     * @see PasswordValidator#verifyStrength(String)
     */
    @Valid
    @NotBlank
    @Size(min = 8)
    @Schema(example = "NovaSenha123")
    private String password;

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
