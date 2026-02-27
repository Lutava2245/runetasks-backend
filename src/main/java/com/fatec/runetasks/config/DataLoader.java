package com.fatec.runetasks.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.*;
import com.fatec.runetasks.exception.ResourceNotFoundException;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final AvatarRepository avatarRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        createAdmin();
    }

    @Transactional
    protected void createAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: ROLE_ADMIN não encontrado."));

            Avatar initialAvatar = avatarRepository.findByIconName("person")
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: Avatar inicial não encontrado."));

            List<Avatar> adminAvatars = avatarRepository.findAll();

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setNickname("AdminRuneTasks");
            admin.setName("Administrador RuneTasks");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setCurrentAvatar(initialAvatar);
            admin.setOwnedAvatars(new HashSet<>(Collections.asSet(adminAvatars)));
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            System.out.println("Usuário Admin inicial criado com sucesso.");
        }
    }
}
