package com.fatec.runetasks.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.AvatarRepository;
import com.fatec.runetasks.domain.repository.RewardRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.InsufficientCoinsException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.StoreService;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void buyAvatar(User user, Long avatarId) {
        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Nenhum avatar encontrado."));

        if (user.getTotalCoins() < avatar.getPrice()) {
            throw new InsufficientCoinsException();
        }

        boolean isOwned = user.getOwnedAvatars().stream()
                .anyMatch(userAvatar -> avatar.getIconName().equals(userAvatar.getIconName()));

        if (isOwned) {
            throw new DuplicateResourceException("Erro: Recompensa já foi resgatada.");
        }

        Set<Avatar> ownedAvatars = user.getOwnedAvatars();
        ownedAvatars.add(avatar);

        user.setTotalCoins(user.getTotalCoins() - avatar.getPrice());
        user.setOwnedAvatars(ownedAvatars);

        List<Reward> rewards = rewardRepository.findByUserId(user.getId());
        rewards.forEach(r -> {
            if (!r.getStatus().equals(RewardStatus.REDEEMED)) {
                r.setStatus(user.getTotalCoins() >= r.getPrice() ? RewardStatus.AVAILABLE : RewardStatus.EXPENSIVE);
                rewardRepository.save(r);
            }
        });

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void claimReward(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Recompensa não encontrada."));
        User user = reward.getUser();

        if (user.getTotalCoins() < reward.getPrice()) {
            throw new InsufficientCoinsException();
        }

        reward.setStatus(RewardStatus.REDEEMED);
        user.setTotalCoins(user.getTotalCoins() - reward.getPrice());

        List<Reward> rewards = rewardRepository.findByUserId(user.getId());
        rewards.forEach(r -> {
            if (!r.getStatus().equals(RewardStatus.REDEEMED) && reward.equals(r)) {
                r.setStatus(user.getTotalCoins() >= r.getPrice() ? RewardStatus.AVAILABLE : RewardStatus.EXPENSIVE);
                rewardRepository.save(r);
            }
        });

        userRepository.save(user);
        rewardRepository.save(reward);
    }

}
