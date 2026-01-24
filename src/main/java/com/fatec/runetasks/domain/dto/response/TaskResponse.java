package com.fatec.runetasks.domain.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TaskResponse {

    private Long id;
    
    private String title;

    private String description;

    private String status;

    private int taskXP;

    private int taskCoins;

    private String skillName;

    private LocalDate date;

    private String repeatType;

}
