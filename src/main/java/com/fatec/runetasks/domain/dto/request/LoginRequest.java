package com.fatec.runetasks.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para a autenticação.
 */
@Data
public class LoginRequest {

    @Valid
    @NotBlank
    private String username;

    @Valid
    @NotBlank
    private String password;

}
