package com.fatec.runetasks.domain.dto.request;

import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.SkillService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Requisição DTO que recebe dados para criar e editar uma {@link Skill} no
 * sistema.
 * 
 * @author Luan T. Felix
 * @see SkillService#createSkill(SkillRequest, User)
 * @see SkillService#updateSkillById(Long, SkillRequest)
 */
@Data
public class SkillRequest {

    /**
     * Nome da habilidade.
     * <p>
     * Não pode ser nulo ou em branco. Deve ter entre 3 e 100 caracteres.
     * <p>
     */
    @Valid
    @NotBlank
    @Size(min = 3, max = 100)
    @Schema(example = "Habilidade de exemplo")
    private String name;

    /**
     * Nome do ícone da habilidade.
     * <p>
     * Não pode ser nulo ou em branco.
     * <p>
     */
    @Valid
    @NotBlank
    @Schema(description = "Nome do ícone da habilidade", example = "habilidade_exemplo")
    private String iconName;

}
