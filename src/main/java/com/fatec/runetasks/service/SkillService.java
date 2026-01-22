package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.SkillRequest;
import com.fatec.runetasks.domain.dto.response.SkillResponse;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.User;

@Service
public interface SkillService {
    
    SkillResponse convertToDTO(Skill skill);

    boolean isOwner(Long skillId, Long userId);

    boolean isOwnerByName(String skillName, Long userId);

    SkillResponse getById(Long id);

    List<SkillResponse> getAll();

    List<SkillResponse> getByUserId(Long id);

    void createSkill(SkillRequest request, User user);

    void updateSkillById(Long id, SkillRequest request);

    void deleteSkillById(Long id);
    
}
