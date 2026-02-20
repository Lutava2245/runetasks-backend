package com.fatec.runetasks.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.response.AvatarResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.AvatarRepository;
import com.fatec.runetasks.service.AvatarService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;

    @Override
    public AvatarResponse convertAvatarToDTO(Avatar avatar, User user) {
        boolean isOwned = false;
        for (Avatar userAvatar : user.getOwnedAvatars()) {
            if (Objects.equals(userAvatar.getId(), avatar.getId())) {
                isOwned = true;
                break;
            }
        }

        return new AvatarResponse(
                avatar.getId(),
                avatar.getTitle(),
                avatar.getIcon(),
                avatar.getPrice(),
                isOwned);
    }

    @Override
    public List<AvatarResponse> getAllAvatars(User user) {
        List<Avatar> avatars = avatarRepository.findAll();

        return avatars.stream()
                .map(avatar -> convertAvatarToDTO(avatar, user))
                .collect(Collectors.toList());
    }

}
