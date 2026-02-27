package com.fatec.runetasks.domain.dto.response;

import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO para representar as informações da entidade {@link Skill}.
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Data
public class SkillResponse {

    /**
     * Identificador único da habilidade.
     */
    @Schema(example = "1")
    private Long id;

    /**
     * Nome da habilidade.
     */
    @Schema(example = "Habilidade de exemplo")
    private String name;

    /**
     * Nome do ícone da habilidade.
     */
    @Schema(description = "Nome do ícone da habilidade", example = "habilidade_exemplo")
    private String icon;

    /**
     * Nível de XP da habilidade.
     */
    @Schema(description = "Nível da habilidade", example = "1")
    private int level;

    /**
     * XP necessário para atingir o próximo nível da habilidade.
     * 
     * @see Skill#getXpToNextLevel()
     */
    @Schema(description = "XP necessário para o próximo nível", example = "120")
    private int xpToNextLevel;

    /**
     * Porcentagem de XP para o próximo nível da habilidade.
     */
    @Schema(description = "Porcentagem de XP para o próximo nível", example = "75")
    private double levelPercentage;

    /**
     * XP ganho até o próximo nível da habilidade.
     */
    @Schema(description = "XP ganho até o próximo nível da habilidade", example = "30")
    private int progressXp;

    /**
     * Total de XP da habilidade.
     */
    @Schema(description = "Total de XP da habilidade", example = "30")
    private int totalXp;

    /**
     * Quantidade de tarefas atreladas a habilidade.
     * 
     * @see Task
     */
    @Schema(description = "Quantidade de tarefas atreladas a habilidade", example = "5")
    private int totalTasks;

}
