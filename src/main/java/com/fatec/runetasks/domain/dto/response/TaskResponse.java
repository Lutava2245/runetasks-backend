package com.fatec.runetasks.domain.dto.response;

import java.time.LocalDate;

import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.domain.model.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO para representar as informações da entidade {@link Task}.
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Data
public class TaskResponse {

    /**
     * Identificador único da tarefa.
     */
    @Schema(example = "1")
    private Long id;
    
    /**
     * Título da tarefa.
     */
    @Schema(example = "Título de exemplo")
    private String title;

    /**
     * Descrição da tarefa.
     */
    @Schema(example = "Descrição de exemplo da tarefa")
    private String description;

    /**
     * Status atual da tarefa.
     * 
     * @see TaskStatus
     */
    @Schema(description = "Status da tarefa", example = "PENDING")
    private String status;

    /**
     * Quantidade de XP dado pela tarefa.
     * 
     * @see Task#getTaskXp()
     */
    @Schema(description = "XP da tarefa", example = "40")
    private int taskXp;

    /**
     * Quantidade de moedas dadas pela tarefa.
     * 
     * @see Task#getTaskCoins()
     */
    @Schema(description = "Moedas da tarefa", example = "15")
    private int taskCoins;

    /**
     * Nome da habilidade atrelada a tarefa.
     * 
     * @see Skill
     */
    @Schema(description = "Nome da habilidade da tarefa", example = "Habilidade de exemplo")
    private String skillName;

    /**
     * Data de vencimento da tarefa.
     */
    @Schema(description = "Data de vencimento da tarefa", example = "20XX-01-01")
    private LocalDate date;

    /**
     * Tipo de recorrência da tarefa.
     * 
     * @see RepeatType
     */
    @Schema(description = "Tipo de recorrência da tarefa", example = "DAILY")
    private String repeatType;

}
