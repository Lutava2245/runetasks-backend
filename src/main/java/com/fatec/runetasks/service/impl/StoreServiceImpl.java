package com.fatec.runetasks.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.AvatarRepository;
import com.fatec.runetasks.domain.repository.RewardRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.event.UserBalanceChangedEvent;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.StoreService;

import lombok.RequiredArgsConstructor;

/**
 * Classe de serviço para operações do sistema de Loja.
 * <p>
 * Contém métodos para lidar com gastos do usuário na loja, como comprar
 * {@link Avatar} e resgatar {@link Reward}.
 * <p>
 * Esta classe é uma implementação concreta da interface {@link StoreService}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Service
public class StoreServiceImpl implements StoreService {

    private final ApplicationEventPublisher eventPublisher;

    private final AvatarRepository avatarRepository;

    private final RewardRepository rewardRepository;

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void buyAvatar(User user, Long avatarId) {
        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Nenhum avatar encontrado."));

        user.spendCoins(avatar.getPrice());
        user.addAvatar(avatar);

        eventPublisher.publishEvent(new UserBalanceChangedEvent(user));

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void claimReward(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Recompensa não encontrada."));
        User user = reward.getUser();

        if (reward.getStatus().equals(RewardStatus.REDEEMED)) {
            throw new DuplicateResourceException("Erro: Recompensa já foi resgatada.");
        }

        user.spendCoins(reward.getPrice());
        reward.setStatus(RewardStatus.REDEEMED);

        eventPublisher.publishEvent(new UserBalanceChangedEvent(user));

        userRepository.save(user);
        rewardRepository.save(reward);
    }

}
