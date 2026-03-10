package com.fatec.runetasks.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.User;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByNameAndUserId(String name, Long id);

    Optional<Skill> findByNameAndUser(String name, User user);

    List<Skill> findByUserId(Long id);

}
