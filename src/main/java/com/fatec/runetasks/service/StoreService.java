package com.fatec.runetasks.service;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.event.UserBalanceChangedEvent;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.InsufficientCoinsException;
import com.fatec.runetasks.exception.ResourceNotFoundException;

/**
 * Interface de serviço para operações do sistema de Loja.
 * <p>
 * Contém métodos para lidar com gastos do usuário na loja, como comprar
 * {@link Avatar} e resgatar {@link Reward}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface StoreService {

    /**
     * Compra um {@link Avatar} para o usuário.
     * <p>
     * O processo verifica se o usuário possui moedas suficientes para comprar o
     * avatar. Caso possua, subtrai o saldo do usuário pelo preço do avatar e
     * adiciona o avatar à sua lista de avatares.
     * <p>
     * Ao comprar um avatar, o sistema emite um {@link UserBalanceChangedEvent} que
     * atualiza o status das recompensas do usuário.
     * <p>
     * 
     * @param user     Usuário que está comprando o avatar.
     * @param avatarId Identificador único do avatar a ser comprado.
     * @throws ResourceNotFoundException  Caso o avatar não seja encontrado.
     * @throws InsufficientCoinsException Caso o usuário não possua moedas
     *                                    suficientes para comprar o avatar.
     * @throws DuplicateResourceException Caso o usuário já possua o avatar
     *                                    comprado.
     * @see RewardService#handleBalanceChange(UserBalanceChangedEvent)
     */
    void buyAvatar(User user, Long avatarId);

    /**
     * Resgata uma {@link Reward} para o usuário.
     * <p>
     * O processo verifica se o usuário possui moedas suficientes para resgatar a
     * recompensa. Caso possua, subtrai o saldo do usuário pelo preço da recompensa
     * e marca a recompensa como {@link RewardStatus#REDEEMED}.
     * <p>
     * Ao resgatar uma recompensa, o sistema emite um
     * {@link UserBalanceChangedEvent} que atualiza o status das recompensas do
     * usuário.
     * <p>
     * 
     * @param rewardId Identificador único da recompensa a ser resgatada.
     * @throws ResourceNotFoundException  Caso a recompensa não seja encontrada.
     * @throws DuplicateResourceException Caso a recompensa já esteja resgatada
     *                                    ({@link RewardStatus#REDEEMED}).
     * @throws InsufficientCoinsException Caso o usuário não possua moedas
     *                                    suficientes para resgatar a recompensa.
     * @see RewardService#handleBalanceChange(UserBalanceChangedEvent)
     */
    void claimReward(Long rewardId);

}
