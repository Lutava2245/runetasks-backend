package com.fatec.runetasks.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.dto.request.UserCreateRequest;
import com.fatec.runetasks.domain.dto.request.UserUpdateRequest;
import com.fatec.runetasks.domain.dto.response.UserResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.AvatarRepository;
import com.fatec.runetasks.domain.repository.RoleRepository;
import com.fatec.runetasks.domain.repository.SkillRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.InvalidPasswordException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.exception.SamePasswordException;
import com.fatec.runetasks.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public UserResponse convertToDTO(User user) {
        double levelPercentage = (user.getProgressXP() * 100) / user.getXpToNextLevel();

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getNickname(),
            user.getEmail(),
            user.getCurrentAvatar().getIcon(),
            user.getCurrentAvatar().getIconName(),
            user.getLevel(),
            user.getXpToNextLevel(),
            levelPercentage,
            user.getProgressXP(),
            user.getTotalXP(),
            user.getTotalCoins(),
            user.getCreatedAt());
    }

    @Override
    public Skill createDefaultSkill(User user) {
        Skill skill = new Skill();
        skill.setName("Habilidade Inicial");
        skill.setIcon("notes");
        skill.setUser(user);
        return skill;
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

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhum usuário encontrado.");
        }

        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createUser(UserCreateRequest request) {
        if (userRepository.existsByEmailOrNickname(request.getEmail(), request.getNickname())) {
            throw new DuplicateResourceException("Erro: Email/Nickname já existentes.");
        }

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
        skillRepository.save(createDefaultSkill(user));
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
    public void changePassword(Long id, ChangePasswordRequest requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));
        
        if (!passwordEncoder.matches(user.getPassword(), requestDTO.getCurrentPassword())) {
            throw new InvalidPasswordException();
        }

        if (passwordEncoder.matches(requestDTO.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException();
        }
        
        String newPassword = requestDTO.getNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

    @Override
    public void selectAvatar(User user, String avatarName) {
        for (Avatar userAvatar : user.getOwnedAvatars()) {
            if (userAvatar.getIconName().equals(avatarName)) {
                Avatar avatar = avatarRepository.findByIconName(avatarName)
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: Nenhum avatar encontrado."));

                user.setCurrentAvatar(avatar);
                userRepository.save(user);
                return;
            }
        }
        throw new ResourceNotFoundException("Erro: Usuário não possui avatar para equipar.");
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        userRepository.delete(user);
    }
    
}
