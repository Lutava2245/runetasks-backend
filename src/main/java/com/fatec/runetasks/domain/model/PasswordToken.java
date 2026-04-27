package com.fatec.runetasks.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um token de recuperação de senha associado a um {@link User}.
 * <p>
 * Este token é gerado quando um usuário solicita a recuperação de senha e é
 * usado para validar a solicitação. Ele possui um valor único, uma data de
 * expiração e está vinculado a um usuário específico. O token é considerado
 * expirado se a data atual for posterior à data de expiração.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "password_tokens")
public class PasswordToken {

    /**
     * Identificador único do token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor string único do token.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Usuário ao qual o token está associado.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Data de expiração do token.
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Verifica se o token está expirado.
     * 
     * @return {@code true} se o token estiver expirado, {@code false} caso
     *         contrário.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

}
