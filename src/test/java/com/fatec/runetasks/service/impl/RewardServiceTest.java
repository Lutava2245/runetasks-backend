package com.fatec.runetasks.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.RewardRepository;
import com.fatec.runetasks.event.UserBalanceChangedEvent;

@ExtendWith(MockitoExtension.class)
public class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;
    
    @InjectMocks
    private RewardServiceImpl rewardService;

    @Test
    @DisplayName("Deve tornar recompensas EXPENSIVE se o saldo do usuário cair")
    void handleBalanceChange_ToExpensive() {
        User user = new User();
        user.setId(1L);
        user.setTotalCoins(20);

        Reward reward = new Reward();
        reward.setPrice(50);
        reward.setStatus(RewardStatus.AVAILABLE);

        when(rewardRepository.findByUserId(1L)).thenReturn(List.of(reward));

        rewardService.handleBalanceChange(new UserBalanceChangedEvent(user));

        assertEquals(RewardStatus.EXPENSIVE, reward.getStatus());
        verify(rewardRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve tornar recompensas AVAILABLE se o saldo do usuário subir")
    void handleBalanceChange_Avaiable() {
        User user = new User();
        user.setId(1L);
        user.setTotalCoins(100);

        Reward reward = new Reward();
        reward.setPrice(75);
        reward.setStatus(RewardStatus.EXPENSIVE);

        when(rewardRepository.findByUserId(1L)).thenReturn(List.of(reward));

        rewardService.handleBalanceChange(new UserBalanceChangedEvent(user));

        assertEquals(RewardStatus.AVAILABLE, reward.getStatus());
        verify(rewardRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve ignorar recompensas que já foram resgatadas")
    void handleBalanceChange_IgnoreRedeemed() {
        User user = new User();
        user.setTotalCoins(100);

        Reward reward = new Reward();
        reward.setStatus(RewardStatus.REDEEMED);

        when(rewardRepository.findByUserId(any())).thenReturn(List.of(reward));

        rewardService.handleBalanceChange(new UserBalanceChangedEvent(user));

        assertEquals(RewardStatus.REDEEMED, reward.getStatus());
    }
}
