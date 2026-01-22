package com.fatec.runetasks.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RewardRequest {

    @Valid
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Valid
    @Size(min = 3, max = 255)
    private String description;

    @Valid
    @NotBlank
    @NotNull
    private int likeLevel;

}
