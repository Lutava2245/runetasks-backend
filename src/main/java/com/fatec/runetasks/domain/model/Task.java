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
 * Representa uma tarefa que pode ser criada e concluída por um usuário
 * {@link User}.
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
     * <p>
     * A quantidade de experiência dada pela tarefa é definida da seguinte forma:
     * <ul>
     * <li>Fácil -> 20 de XP</li>
     * <li>Média -> 40 de XP</li>
     * <li>Difícil -> 60 de XP</li>
     * </ul>
     * <p>
     * 
     * @return um {@code int} do valor do XP
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
     * <p>
     * A quantidade de moedas dadas pela tarefa são definidas da seguinte forma:
     * <ul>
     * <li>Fácil -> 5 moedas</li>
     * <li>Média -> 15 moedas</li>
     * <li>Difícil -> 25 moedas</li>
     * </ul>
     * <p>
     * 
     * @return um {@code int} do número de moedas
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
     * ocorrência e o status é definido como {@link TaskStatus#PENDING}.
     * <p>
     * Dependendo o tipo de recorrência da tarefa, a nova data é definida:
     * <ul>
     * <li>
     * Diária → data é atualizada para o dia atual
     * </li>
     * <li>
     * Semanal → data é atualizada para o próximo dia da semana correspondente
     * </li>
     * <li>
     * Mensal → data é atualizada para o próximo dia do mês correspondente
     * </li>
     * </ul>
     * 
     * @see RepeatType
     */
    public void prepareNextOccurrence() {
        if (this.repeatType == RepeatType.NONE) {
            return;
        }

        LocalDate nextDate = this.date;

        switch (this.repeatType) {
            case DAILY -> nextDate = LocalDate.now();
            case WEEKLY -> {
                while (nextDate.isBefore(LocalDate.now())) {
                    nextDate = nextDate.plusWeeks(1);
                }
            }
            case MONTHLY -> {
                while (nextDate.isBefore(LocalDate.now())) {
                    nextDate = nextDate.plusMonths(1);
                }
            }
            default -> {
            }
        }

        this.date = nextDate;
        this.status = TaskStatus.PENDING;
    }
}
