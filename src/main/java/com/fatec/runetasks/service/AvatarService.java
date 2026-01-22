package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.response.AvatarResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.User;

@Service
public interface AvatarService {
    
    AvatarResponse convertAvatarToDTO(Avatar avatar, User user);

    boolean isOwned(String name, Long userId);

    List<AvatarResponse> getAllAvatars(User user);

}
