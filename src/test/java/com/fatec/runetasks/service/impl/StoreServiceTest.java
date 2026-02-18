package com.fatec.runetasks.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.AvatarRepository;
import com.fatec.runetasks.domain.repository.RewardRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.event.UserBalanceChangedEvent;
import com.fatec.runetasks.exception.InsufficientCoinsException;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private AvatarRepository avatarRepository;
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    @DisplayName("Deve comprar avatar com sucesso e disparar evento de saldo")
    void buyAvatar_Success() {
        Avatar userAvatar = new Avatar();
        userAvatar.setIcon("UserAvatar");

        User user = new User();
        user.setTotalCoins(130);
        user.setOwnedAvatars(new HashSet<>(Set.of(userAvatar)));

        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setPrice(100);
        avatar.setIcon("NewAvatar");

        when(avatarRepository.findById(1L)).thenReturn(Optional.of(avatar));

        storeService.buyAvatar(user, 1L);

        assertEquals(30, user.getTotalCoins());
        assertTrue(user.getOwnedAvatars().contains(avatar));
        verify(eventPublisher, times(1)).publishEvent(any(UserBalanceChangedEvent.class));
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção se o saldo for insuficiente para o avatar")
    void buyAvatar_InsufficientCoins() {
        User user = new User();
        user.setTotalCoins(15);

        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setPrice(150);

        when(avatarRepository.findById(1L)).thenReturn(Optional.of(avatar));

        assertThrows(InsufficientCoinsException.class, () -> storeService.buyAvatar(user, 1L));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve resgatar recompensa e atualizar status para REDEEMED")
    void claimReward_Success() {
        User user = new User();
        user.setTotalCoins(100);

        Reward reward = new Reward();
        reward.setPrice(75);
        reward.setStatus(RewardStatus.AVAILABLE);
        reward.setUser(user);

        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));

        storeService.claimReward(1L);

        assertEquals(25, user.getTotalCoins());
        assertEquals(RewardStatus.REDEEMED, reward.getStatus());
        verify(eventPublisher).publishEvent(any(UserBalanceChangedEvent.class));
    }
}
