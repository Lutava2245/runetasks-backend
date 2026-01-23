package com.fatec.runetasks.domain.dto.request;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskCreateRequest {
    
    @Valid
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Valid
    @Size(min = 3, max = 255)
    private String description;

    @Valid
    @NotNull
    private String difficulty;

    @Valid
    @NotNull
    private String skillName;

    @Valid
    private LocalDate date;

    @Valid
    private String repeatType;

}
