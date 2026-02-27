package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.RewardService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para registro de uma {@link Reward} no
 * sistema.
 * 
 * @author Luan T. Felix
 * @see RewardService#createReward(RewardCreateRequest, User)
 */
@Data
public class RewardCreateRequest {

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

    /**
     * Valor {@code int} que representa o quanto o usuário gosta da recompensa.
     * <p>
     * Não pode ser nulo. O valor deve ser entre 1 e 5.
     * <p>
     */
    @Valid
    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Nível de gosto do usuário pela recompensa", example = "3")
    private int likeLevel;

}
