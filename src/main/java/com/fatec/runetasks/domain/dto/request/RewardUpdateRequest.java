package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.service.RewardService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para atualizar as informações de uma
 * {@link Reward}.
 * 
 * @author Luan T. Felix
 * @see RewardService#updateRewardById(Long, RewardUpdateRequest)
 */
@Data
public class RewardUpdateRequest {

    /**
     * Título da recompensa.
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
     * Descrição da recompensa.
     * <p>
     * Deve ter entre 3 e 255 caracteres.
     * <p>
     */
    @Valid
    @Size(min = 3, max = 255)
    @Schema(example = "Descrição de exemplo da recompensa")
    private String description;

}
