package com.fatec.runetasks.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.dto.request.UserCreateRequest;
import com.fatec.runetasks.domain.dto.request.UserUpdateRequest;
import com.fatec.runetasks.domain.dto.response.UserResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.*;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.InvalidPasswordException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.exception.SamePasswordException;
import com.fatec.runetasks.service.UserService;
import com.fatec.runetasks.util.PasswordValidator;

import lombok.RequiredArgsConstructor;

/**
 * Classe de serviço para operações da entidade {@link User}.
 * <p>
 * Contém métodos para o gerenciamento de usuários e seus estados, como
 * encontrar usuários, trocar senhas, registrá-los, entre outros. Também possui
 * métodos auxiliares como conversão dos dados da entidade para DTO e validação
 * de senhas.
 * <p>
 * Esta classe é uma implementação concreta da interface {@link UserService}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final RewardRepository rewardRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AvatarRepository avatarRepository;

    private final PasswordValidator passwordValidator;

    @Override
    public UserResponse convertToDTO(User user) {
        double levelPercentage = (double) (user.getProgressXp() * 100) / user.getXpToNextLevel();
        int unlockableItems = 0;
        List<Avatar> avatars = avatarRepository.findByPriceLessThanEqual(user.getTotalCoins());
        List<Reward> rewards = rewardRepository.findByUserIdAndPriceLessThanEqual(user.getId(), user.getTotalCoins());

        for (Avatar avatar : avatars) {
            boolean isOwned = false;
            for (Avatar userAvatar : user.getOwnedAvatars()) {
                if (Objects.equals(userAvatar.getId(), avatar.getId())) {
                    isOwned = true;
                    break;
                }
            }
            if (!isOwned) {
                unlockableItems++;
            }
        }

        for (Reward reward : rewards) {
            if (reward.getStatus().equals(RewardStatus.AVAILABLE)) {
                unlockableItems++;
            }
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getCurrentAvatar().getIconName(),
                user.getLevel(),
                user.getXpToNextLevel(),
                levelPercentage,
                user.getProgressXp(),
                user.getTotalXp(),
                user.getTotalCoins(),
                unlockableItems,
                user.getCreatedAt());
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        return convertToDTO(user);
    }

    @Override
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createUser(UserCreateRequest request) {
        if (userRepository.existsByEmailOrNickname(request.getEmail(), request.getNickname())) {
            throw new DuplicateResourceException("Erro: Email/Nickname já existentes.");
        }

        passwordValidator.verifyStrength(request.getPassword());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Role padrão não encontrado."));
        Avatar initialAvatar = avatarRepository.findByIconName("person")
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Avatar inicial não encontrado."));

        User user = new User();
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setCurrentAvatar(initialAvatar);
        user.setPassword(hashedPassword);
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        user.setOwnedAvatars(new HashSet<>(Collections.singletonList(initialAvatar)));

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUserById(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        user.setName(request.getName());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException();
        }

        passwordValidator.verifyStrength(request.getNewPassword());

        String newPassword = request.getNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

    @Override
    public void selectAvatar(User user, String avatarName) {
        Avatar selectedAvatar = avatarRepository.findByIconName(avatarName)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Avatar não encontrado."));

        if (user.hasAvatar(selectedAvatar)) {
            user.setCurrentAvatar(selectedAvatar);
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("Erro: Usuário não possui avatar para equipar.");
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        userRepository.delete(user);
    }

}
