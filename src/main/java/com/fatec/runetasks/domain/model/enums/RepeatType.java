package com.fatec.runetasks.domain.model.enums;

import com.fatec.runetasks.domain.model.Task;

/**
 * Representa os tipos de repetição de uma {@link Task}.
 * <p>
 * Os tipos de repetição são:
 * <ul>
 * <li>{@link #NONE}: A tarefa não se repete.</li>
 * <li>{@link #DAILY}: A tarefa se repete diariamente.</li>
 * <li>{@link #WEEKLY}: A tarefa se repete semanalmente.</li>
 * <li>{@link #MONTHLY}: A tarefa se repete mensalmente.</li>
 * </ul>
 * <p>
 * 
 * @author Luan T. Felix
 */
public enum RepeatType {
    NONE, DAILY, WEEKLY, MONTHLY
}
