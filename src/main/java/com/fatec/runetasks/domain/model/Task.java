package com.fatec.runetasks.domain.model;

import java.time.LocalDate;

import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.domain.model.enums.TaskDifficulty;
import com.fatec.runetasks.domain.model.enums.TaskStatus;

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
 * Representa uma tarefa que pode ser criada pelo {@link User} e concluída por
 * ele.
 * <p>
 * Uma tarefa é um item que serve para ser concluído pelo usuário. Ao completar
 * tarefas, o usuário ganha experiência e moedas, e a {@link Skill} relacionada
 * ganha pontos de experiência.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "tasks")
public class Task {

    /**
     * Identificador único da tarefa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título da tarefa.
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Descrição da tarefa.
     */
    @Column
    private String description;

    /**
     * Status da tarefa.
     * 
     * @see TaskStatus
     */
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * Dificuldade da tarefa.
     * 
     * @see TaskDifficulty
     */
    @Enumerated(EnumType.STRING)
    private TaskDifficulty difficulty;

    /**
     * Data da tarefa.
     */
    @Column
    private LocalDate date = LocalDate.now();

    /**
     * Tipo de recorrência da tarefa.
     * 
     * @see RepeatType
     */
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    /**
     * Usuário que criou a tarefa.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Habilidade relacionada à tarefa.
     */
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    /**
     * Retorna o XP dado pela tarefa de acordo com {@link TaskDifficulty}.
     * 
     * @return um <code>int</code> do valor do XP
     */
    public int getTaskXp() {
        return switch (this.difficulty) {
            case EASY -> 20;
            case MEDIUM -> 40;
            case HARD -> 60;
        };
    }

    /**
     * Retorna as modeas dadas pela tarefa de acordo com {@link TaskDifficulty}.
     * 
     * @return um <code>int</code> do número de moedas
     */
    public int getTaskCoins() {
        return switch (this.difficulty) {
            case EASY -> 5;
            case MEDIUM -> 15;
            case HARD -> 25;
        };
    }

    /**
     * Prepara a próxima ocorrência da tarefa.
     * <p>
     * Se a tarefa for recorrente, a data da tarefa é atualizada para a próxima
     * ocorrência e o status é definido como <code>TaskStatus.PENDING</code>.
     * <p>
     * 
     * @see RepeatType
     * @see TaskStatus
     */
    public void prepareNextOccurrence() {
        if (this.repeatType == RepeatType.NONE) {
            return;
        }

        this.date = switch (this.repeatType) {
            case DAILY -> this.date.plusDays(1);
            case WEEKLY -> this.date.plusWeeks(1);
            case MONTHLY -> this.date.plusMonths(1);
            default -> this.date;
        };

        this.status = TaskStatus.PENDING;
    }
}
