package com.fatec.runetasks.domain.dto.request;

import java.time.LocalDate;

import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.service.TaskService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para atualizar as informações de uma
 * {@link Task} no sistema.
 * 
 * @author Luan T. Felix
 * @see TaskService#updateTaskById(Long, TaskUpdateRequest)
 */
@Data
public class TaskUpdateRequest {

    /**
     * Título da tarefa.
     * <p>
     * Não pode ser nulo ou em branco. Deve ter entre 3 e 100 caracteres.
     * <p>
     */
    @Valid
    @NotBlank
    @Size(min = 3, max = 100)
    @Schema(example = "Título de exemplo")
    private String title;

    /**
     * Descrição da tarefa.
     * <p>
     * Deve ter entre 3 e 100 caracteres.
     * <p>
     */
    @Valid
    @Size(min = 3, max = 255)
    @Schema(example = "Descrição de exemplo da tarefa")
    private String description;

    /**
     * Data de vencimento da tarefa.
     * <p>
     * Não pode ser nulo.
     * <p>
     */
    @Valid
    @NotNull
    @Schema(description = "Data de vencimento da tarefa", example = "20XX-01-01")
    private LocalDate date;

    /**
     * Tipo de recorrência da tarefa.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     * 
     * @see RepeatType
     */
    @Valid
    @NotBlank
    @Schema(description = "Tipo de recorrência da tarefa", example = "DAILY")
    private String repeatType;

}
