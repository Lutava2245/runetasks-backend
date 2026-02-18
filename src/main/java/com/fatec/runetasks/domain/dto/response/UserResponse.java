package com.fatec.runetasks.domain.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserResponse {

    private Long id;

    private String name;
    
    private String nickname;

    private String email;

    private String currentAvatarIcon;

    private String currentAvatarName;

    private int level;

    private int xpToNextLevel;

    private double levelPercentage;

    private double progressXp;

    private int totalXp;
    
    private int totalCoins;

    private int unlockableItems;

    private LocalDate createdAt;
    
}
