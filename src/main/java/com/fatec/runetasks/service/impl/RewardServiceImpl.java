package com.fatec.runetasks.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.dto.request.RewardCreateRequest;
import com.fatec.runetasks.domain.dto.request.RewardUpdateRequest;
import com.fatec.runetasks.domain.dto.response.RewardResponse;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.RewardRepository;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.RewardService;

@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Override
    public RewardResponse convertToDTO(Reward reward) {
        return new RewardResponse(
                reward.getId(),
                reward.getTitle(),
                reward.getDescription(),
                reward.getPrice(),
                reward.getStatus().name());
    }

    @Override
    public boolean isOwner(Long rewardId, Long userId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Recompensa não encontrada."));

        return reward.getUser().getId().equals(userId);
    }

    @Override
    public RewardResponse getById(Long id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Recompensa não encontrada."));

        return convertToDTO(reward);
    }

    @Override
    public List<RewardResponse> getAll() {
        List<Reward> rewards = rewardRepository.findAll();

        return rewards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardResponse> getByUserId(Long id) {
        List<Reward> rewards = rewardRepository.findByUserId(id);

        return rewards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createReward(RewardCreateRequest request, User user) {
        int price = switch (request.getLikeLevel()) {
            case 2 -> 50;
            case 3 -> 75;
            case 4 -> 100;
            case 5 -> 150;
            default -> 30;
        };

        Reward reward = new Reward();
        reward.setTitle(request.getTitle());
        reward.setDescription(request.getDescription());
        reward.setPrice(price);
        reward.setStatus(user.getTotalCoins() >= price ? RewardStatus.AVAILABLE : RewardStatus.EXPENSIVE);
        reward.setUser(user);

        rewardRepository.save(reward);
    }

    @Transactional
    @Override
    public void updateRewardById(Long id, RewardUpdateRequest request) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Recompensa não encontrada."));

        reward.setTitle(request.getTitle());
        reward.setDescription(request.getDescription());

        rewardRepository.save(reward);
    }

    @Override
    public void deleteRewardById(Long id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Recompensa não encontrada."));

        rewardRepository.delete(reward);
    }

}
