package com.fatec.runetasks.domain.dto.response;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe DTO para representar as informações da entidade {@link Avatar}.
 * 
 * @author Luan T. Felix
 */
@AllArgsConstructor
@Data
public class AvatarResponse {

    /**
     * Identificador único do avatar.
     */
    @Schema(example = "1")
    private Long id;

    /**
     * Título do avatar.
     */
    @Schema(example = "Avatar do Usuário")
    private String title;

    /**
     * Nome do ícone do avatar.
     */
    @Schema(description = "Nome do ícone do avatar", example = "user_avatar")
    private String iconName;

    /**
     * Preço do avatar.
     */
    @Schema(example = "100")
    private int price;

    /**
     * {@code true} se o usuário possui o avatar, {@code false} caso contrário.
     * 
     * @see User
     */
    @Schema(description = "Se o usuário possui o avatar", example = "true")
    private boolean owned;

}
