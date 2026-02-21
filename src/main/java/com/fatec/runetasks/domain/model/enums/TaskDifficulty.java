package com.fatec.runetasks.domain.model.enums;

/**
 * Representa as dificuldades disponíveis de uma {@link Task}.
 * <p>
 * As dificuldades são:
 * <ul>
 * <li>{@link #EASY}: A tarefa é fácil e dará poucas moedas e XP.</li>
 * <li>{@link #MEDIUM}: A tarefa é mediana e dará algumas moedas e XP.</li>
 * <li>{@link #HARD}: A tarefa é difícil e dará uma quantidade razoável de
 * moedas e XP.</li>
 * </ul>
 * <p>
 * 
 * @author Luan T. Felix
 */
public enum TaskDifficulty {
    EASY, MEDIUM, HARD
}
