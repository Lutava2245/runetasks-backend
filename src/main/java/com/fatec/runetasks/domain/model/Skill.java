package com.fatec.runetasks.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Representa uma habilidade que pode ser desenvolvida pelo {@link User}.
 * <p>
 * Uma habilidade é um conjunto de tarefas que pode ser desenvolvida pelo
 * usuário.
 * Ela contém pontos de experiência e um sistema de níveis que o usuário pode
 * ganhar ao completar uma {@link Task}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "skills")
public class Skill {

    /**
     * Identificador único da habilidade.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome da habilidade.
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * Ícone da habilidade.
     */
    @Column(nullable = false)
    private String icon;

    /**
     * Total de pontos de experiência do usuário.
     */
    @Column
    private int totalXp = 0;

    /**
     * Nível do usuário na habilidade.
     */
    @Column
    private int level = 1;

    /**
     * Pontos de experiência do usuário na habilidade.
     */
    @Column
    private int progressXp = 0;

    /**
     * Usuário que possui a habilidade.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Retorna o total de XP necessário para atingir o pŕoximo nível.
     * <p>
     * O XP necessário é calculado de acordo com o nível atual da habilidade
     * <p>
     * 
     * @return um <code>int</code> do total de XP para subir de nível
     */
    public int getXpToNextLevel() {
        return 20 + (10 * level);
    }

    /**
     * Adiciona pontos de experiência da habilidade ao usuário.
     * <p>
     * Se a habilidade atingir o nível máximo, os pontos de experiência são
     * resetados e o nível é incrementado.
     * <p>
     * 
     * @param xp Pontos de experiência a serem adicionados.
     */
    public void addXp(final int xp) {
        this.totalXp += xp;
        this.progressXp += xp;

        while (this.progressXp >= getXpToNextLevel()) {
            this.progressXp -= getXpToNextLevel();
            this.level++;
        }
    }
}
