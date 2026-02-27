package com.fatec.runetasks.domain.dto.response;

import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.enums.RewardStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO para representar as informações da entidade {@link Reward}.
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Data
public class RewardResponse {

    /**
     * Identificador único da recompensa.
     */
    @Schema(example = "1")
    private Long id;

    /**
     * Título da recompensa.
     */
    @Schema(example = "Título de exemplo")
    private String title;

    /**
     * Descrição da recompensa.
     */
    @Schema(example = "Descrição de exemplo da recompensa")
    private String description;

    /**
     * Preço da recompensa.
     */
    @Schema(example = "75")
    private int price;

    /**
     * Status atual da recompensa.
     * 
     * @see RewardStatus
     */
    @Schema(description = "Status da recompensa", example = "AVAILABLE")
    private String status;

}
