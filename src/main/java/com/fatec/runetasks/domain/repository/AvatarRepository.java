package com.fatec.runetasks.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.runetasks.domain.model.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    boolean existsByIconName(String name);

    Optional<Avatar> findByTitle(String name);
    
    Optional<Avatar> findByIconName(String name);

    List<Avatar> findByPriceLessThanEqual(int price);

}
