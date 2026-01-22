package com.fatec.runetasks.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequest {

    @Valid
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @Valid
    @NotBlank
    private String icon;
    
}
