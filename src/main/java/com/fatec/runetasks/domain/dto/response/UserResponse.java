package com.fatec.runetasks.domain.dto.response;

import java.time.LocalDate;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.StoreService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO para representar as informações da entidade {@link User}.
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Data
public class UserResponse {

    /**
     * Identificador único do avatar.
     */
    @Schema(example = "1")
    private Long id;

    /**
     * Nome do usuário.
     */
    @Schema(example = "Usuário RuneTasks")
    private String name;

    /**
     * Nickname do usuário.
     */
    @Schema(example = "user_runetasks")
    private String nickname;

    /**
     * Email do usuário.
     */
    @Schema(example = "user@runetasks.com")
    private String email;

    /**
     * Avatar atual do usuário.
     * 
     * @see Avatar
     */
    @Schema(description = "Avatar atual do usuário", example = "user_avatar")
    private String currentAvatar;

    /**
     * Nível de XP do usuário.
     */
    @Schema(description = "Nível do usuário", example = "1")
    private int level;

    /**
     * XP necessário para atingir o próximo nível do usuário.
     * 
     * @see User#getXpToNextLevel()
     */
    @Schema(description = "XP necessário para o próximo nível", example = "120")
    private int xpToNextLevel;

    /**
     * Porcentagem de XP para o próximo nível do usuário.
     */
    @Schema(description = "Porcentagem de XP para o próximo nível", example = "75")
    private double levelPercentage;

    /**
     * XP ganho até o próximo nível do usuário.
     */
    @Schema(description = "XP ganho até o próximo nível do usuário", example = "30")
    private int progressXp;

    /**
     * Total de XP do usuário.
     */
    @Schema(description = "Total de XP do usuário", example = "30")
    private int totalXp;

    /**
     * Total de moedas do usuário.
     */
    @Schema(description = "Total de moedas do usuário", example = "55")
    private int totalCoins;

    /**
     * Quantidade de itens disponíveis na loja.
     * 
     * @see StoreService
     */
    @Schema(description = "Quantidade de itens disponíveis na loja", example = "2")
    private int unlockableItems;

    /**
     * Data de criação do usuário
     */
    @Schema(description = "Data de criação do usuário", example = "20XX-01-01")
    private LocalDate createdAt;

}
