package com.fatec.runetasks.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.response.AvatarResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.AvatarRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.AvatarService;

@Service
public class AvatarServiceImpl implements AvatarService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Override
    public AvatarResponse convertAvatarToDTO(Avatar avatar, User user) {
        boolean isOwned = isOwned(avatar.getTitle(), user.getId());

        return new AvatarResponse(
            avatar.getId(),
            avatar.getTitle(),
            avatar.getIconName(),
            avatar.getIcon(),
            avatar.getPrice(),
            isOwned);
    }

    @Override
    public boolean isOwned(String name, Long userId) {
        Avatar avatar = avatarRepository.findByTitle(name)
            .orElseThrow(() -> new ResourceNotFoundException("Erro: Avatar não encontrado."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        boolean owned = false;
        for (Avatar userAvatar : user.getOwnedAvatars()) {
            if (userAvatar.getId() == avatar.getId()) {
                owned = true;
            }
        }
        return owned;
    }
    
    @Override
    public List<AvatarResponse> getAllAvatars(User user) {
        List<Avatar> avatars = avatarRepository.findAll();

        if (avatars.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhum avatar encontrado.");
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            avatars = avatars.stream()
                    .filter(avatar -> !avatar.getIconName().equals("adm"))
                    .collect(Collectors.toList());
        }
        
        return avatars.stream()
                .map(avatar -> convertAvatarToDTO(avatar, user))
                .collect(Collectors.toList());
    }

}
