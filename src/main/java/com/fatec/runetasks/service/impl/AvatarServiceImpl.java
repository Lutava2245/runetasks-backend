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

/**
 * Classe de serviço para operações da entidade {@link Avatar}.
 * <p>
 * Contém métodos para encontrar avatares na loja e para converter dos dados da
 * entidade para DTO.
 * <p>
 * Esta classe é uma implementação concreta da interface {@link AvatarService}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;

    @Override
    public AvatarResponse convertToDTO(Avatar avatar, User user) {
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
                avatar.getIconName(),
                avatar.getPrice(),
                isOwned);
    }

    @Override
    public List<AvatarResponse> getAll(User user) {
        List<Avatar> avatars = avatarRepository.findAll();

        return avatars.stream()
                .map(avatar -> convertToDTO(avatar, user))
                .collect(Collectors.toList());
    }

}
