package com.fatec.runetasks.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Email
    @Valid
    @NotBlank
    @Size(min = 5, max = 100)
    private String email;

}
