package com.fatec.runetasks.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.dto.request.SkillRequest;
import com.fatec.runetasks.domain.dto.response.SkillResponse;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.SkillRepository;
import com.fatec.runetasks.domain.repository.TaskRepository;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.SkillService;

import lombok.RequiredArgsConstructor;

/**
 * Classe de serviço para operações da entidade {@link Skill}.
 * <p>
 * Contém métodos para o gerenciamento de habilidades e seus estados, como
 * encontrar habilidades, excluir tarefas, editá-las, entre outros. Também
 * possui métodos auxiliares como conversão dos dados da entidade para DTO e
 * verificação de quem é o dono da habilidade.
 * <p>
 * Esta classe é uma implementação concreta da interface {@link SkillService}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    private final TaskRepository taskRepository;

    @Override
    public SkillResponse convertToDTO(Skill skill) {
        List<Task> tasks = taskRepository.findBySkillId(skill.getId());

        int levelPercentage = (skill.getProgressXp() * 100) / skill.getXpToNextLevel();

        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getIcon(),
                skill.getLevel(),
                skill.getXpToNextLevel(),
                levelPercentage,
                skill.getProgressXp(),
                skill.getTotalXp(),
                tasks.size());
    }

    @Override
    public boolean isOwner(Long skillId, Long userId) {
        Skill skill = skillRepository.findById(skillId)
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

        return skills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillResponse> getByUserId(Long id) {
        List<Skill> skills = skillRepository.findByUserId(id);

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

        if (!skill.getName().equals(request.getName())
                && skillRepository.existsByNameAndUser(request.getName(), skill.getUser())) {
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

        taskRepository.deleteAll(taskRepository.findBySkillId(id));
        skillRepository.delete(skill);
    }

}
