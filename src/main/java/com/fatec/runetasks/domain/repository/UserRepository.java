package com.fatec.runetasks.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.runetasks.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailOrNickname(String email, String nickname);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailOrNickname(String email, String nickname);
    
}
