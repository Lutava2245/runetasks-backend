package com.fatec.runetasks.domain.model;

import com.fatec.runetasks.domain.model.enums.RewardStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa uma recompensa que pode ser criada pelo {@link User} e resgatada
 * por ele
 * <p>
 * Uma recompensa é um item que serve para ser trocado por moedas após a
 * conclusão de tarefas.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "rewards")
public class Reward {

    /**
     * Identificador único da recompensa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título da recompensa.
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Descrição da recompensa.
     */
    @Column
    private String description;

    /**
     * Preço da recompensa.
     */
    @Column(nullable = false)
    private int price;

    /**
     * Status da recompensa.
     */
    @Enumerated(EnumType.STRING)
    private RewardStatus status;

    /**
     * Usuário que criou a recompensa.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
