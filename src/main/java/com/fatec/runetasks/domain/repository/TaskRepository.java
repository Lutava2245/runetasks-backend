package com.fatec.runetasks.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    boolean existsByTitleAndUser(String title, User user);

    boolean existsBySkill(Skill skill);

    Optional<Task> findByTitle(String title);

    List<Task> findBySkillId(Long id);

    List<Task> findByUserId(Long id);

}
