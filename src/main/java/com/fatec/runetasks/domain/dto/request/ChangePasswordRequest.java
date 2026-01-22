package com.fatec.runetasks.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Valid
    @NotBlank
    private String currentPassword;

    @Valid
    @NotBlank
    private String newPassword;

}
