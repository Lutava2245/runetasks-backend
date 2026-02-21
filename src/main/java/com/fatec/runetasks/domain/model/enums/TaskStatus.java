package com.fatec.runetasks.domain.model.enums;

import com.fatec.runetasks.domain.model.Task;

/**
 * Representa os status de uma {@link Task}.
 * <p>
 * Os status são:
 * <ul>
 * <li>{@link #PENDING}: A tarefa está pendente.</li>
 * <li>{@link #BLOCKED}: A tarefa está bloqueada.</li>
 * <li>{@link #COMPLETED}: A tarefa foi concluída.</li>
 * </ul>
 * <p>
 * 
 * @author Luan T. Felix
 */
public enum TaskStatus {
    PENDING, BLOCKED, COMPLETED
}
