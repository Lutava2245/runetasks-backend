package com.fatec.runetasks.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.dto.request.SkillRequest;
import com.fatec.runetasks.domain.dto.response.SkillResponse;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.SkillRepository;
import com.fatec.runetasks.domain.repository.TaskRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.SkillService;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public SkillResponse convertToDTO(Skill skill) {
        List<Task> tasks = taskRepository.findBySkillId(skill.getId());

        int levelPercentage = (skill.getProgressXP() * 100) / skill.getXpToNextLevel();

        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getIcon(),
            skill.getLevel(),
            skill.getXpToNextLevel(),
            levelPercentage,
            skill.getProgressXP(),
            skill.getTotalXP(),
            tasks.size());
    }
    
    @Override
    public boolean isOwner(Long skillId, Long userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Habilidade não encontrada."));

        return skill.getUser().getId().equals(userId);
    }

    @Override
    public boolean isOwnerByName(String skillName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));
        Skill skill = skillRepository.findByNameAndUser(skillName, user)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Habilidade não encontrada."));

        return skill.getUser().getId().equals(userId);
    }

    @Override
    public SkillResponse getById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Habilidade não encontrada."));

        return convertToDTO(skill);
    }

    @Override
    public List<SkillResponse> getAll() {
        List<Skill> skills = skillRepository.findAll();

        if (skills.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhuma habilidade encontrada.");
        }

        return skills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillResponse> getByUserId(Long id) {
        List<Skill> skills = skillRepository.findByUserId(id);

        if (skills.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhuma habilidade encontrada.");
        }

        return skills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createSkill(SkillRequest request, User user) {
        if (skillRepository.existsByNameAndUser(request.getName(), user)) {
            throw new DuplicateResourceException("Erro: Habilidade de mesmo nome já existente.");
        }

        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setIcon(request.getIcon());
        skill.setUser(user);

        skillRepository.save(skill);
    }

    @Transactional
    @Override
    public void updateSkillById(Long id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Habilidade não encontrada."));

        if (skillRepository.existsByNameAndUser(request.getName(), skill.getUser())) {
            throw new DuplicateResourceException("Erro: Habilidade de mesmo nome já existente.");
        }

        skill.setName(request.getName());
        skill.setIcon(request.getIcon());

        skillRepository.save(skill);
    }

    @Transactional
    @Override
    public void deleteSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Habilidade não encontrada."));

        skillRepository.delete(skill);
    }

}
