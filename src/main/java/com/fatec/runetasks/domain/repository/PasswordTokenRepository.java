package com.fatec.runetasks.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.runetasks.domain.model.PasswordToken;
import com.fatec.runetasks.domain.model.User;
import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

    boolean existsByUser(User user);

    Optional<PasswordToken> findByToken(String token);

    void deleteByUser(User user);

}
