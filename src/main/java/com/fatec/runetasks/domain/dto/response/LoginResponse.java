package com.fatec.runetasks.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO que representa o token JWT gerado após autenticação.
 */
@AllArgsConstructor
@Data
public class LoginResponse {

    private String jwtToken;

}
