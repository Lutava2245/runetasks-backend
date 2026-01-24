package com.fatec.runetasks.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.runetasks.domain.model.Reward;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    
    List<Reward> findByUserId(Long id);

    List<Reward> findByUserIdAndPriceLessThanEqual(Long id, int price);

}
