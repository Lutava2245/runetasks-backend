package com.fatec.runetasks.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Valid
    @NotBlank
    private String resetToken;

    @Valid
    @NotBlank
    private String newPassword;
    
}
