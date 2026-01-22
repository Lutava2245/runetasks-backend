package com.fatec.runetasks.service;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.model.User;

@Service
public interface StoreService {

    void buyAvatar(User user, Long avatarId);

    void claimReward(Long rewardId);
    
}
