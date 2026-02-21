package com.fatec.runetasks.domain.model.enums;

import com.fatec.runetasks.domain.model.Reward;

/**
 * Representa os status de uma {@link Reward}.
 * <p>
 * Os status são:
 * <ul>
 * <li>{@link #AVAILABLE}: A recompensa está disponível para compra.</li>
 * <li>{@link #EXPENSIVE}: A recompensa está disponível para compra, mas o
 * usuário não tem moedas suficientes.</li>
 * <li>{@link #REDEEMED}: A recompensa foi comprada.</li>
 * </ul>
 * <p>
 * 
 * @author Luan T. Felix
 */
public enum RewardStatus {
    AVAILABLE, EXPENSIVE, REDEEMED
}
