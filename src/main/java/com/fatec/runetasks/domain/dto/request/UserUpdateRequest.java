package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.service.UserService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para atualizar as informações de um
 * {@link User}.
 * 
 * @author Luan T. Felix
 * @see UserService#updateUserById(Long, UserUpdateRequest)
 */
@Data
public class UserUpdateRequest {

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

}
