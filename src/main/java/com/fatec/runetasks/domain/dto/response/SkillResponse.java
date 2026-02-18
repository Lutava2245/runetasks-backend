package com.fatec.runetasks.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SkillResponse {

    private Long id;
    
    private String name;

    private String icon;

    private int level;

    private int xpToNextLevel;

    private double levelPercentage;

    private int progressXp;
    
    private int totalXp;

    private int totalTasks;

}
