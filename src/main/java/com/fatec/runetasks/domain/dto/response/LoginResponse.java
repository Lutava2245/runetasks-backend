package com.fatec.runetasks.domain.dto.response;

import com.fatec.runetasks.domain.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO que representa o token JWT gerado após autenticação de um
 * {@link User}.
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Data
public class LoginResponse {

    private String jwtToken;

}
