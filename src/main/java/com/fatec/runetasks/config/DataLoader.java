package com.fatec.runetasks.config;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.domain.repository.*;
import com.fatec.runetasks.exception.ResourceNotFoundException;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DataLoader {

    private final RoleRepository roleRepository;

    private final AvatarRepository avatarRepository;

    private final UserRepository userRepository;

    private final SkillRepository skillRepository;

    private final TaskRepository taskRepository;

    private final RewardRepository rewardRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            createRoles();
            createAvatars();
            createInitialAdmin();
            createAdminSkill();
            createAdminTask();
            createAdminReward();
        };
    }

    private void createRoles() {
        List<String> requiredRoles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        for (String roleName : requiredRoles) {
            if (roleRepository.findByName(roleName).isEmpty()) {

                Role role = new Role();
                role.setName(roleName);

                roleRepository.save(role);
                System.out.println("Role criado: " + roleName);
            }
        }
    }

    private void createAvatars() {
        if (avatarRepository.findAll().isEmpty()) {
            Map<String, String> avatarMap = new LinkedHashMap<>();
            avatarMap.put("person", "👤");
            avatarMap.put("wizard", "🧙");
            avatarMap.put("crown", "👑");
            avatarMap.put("knight", "⚔️");
            avatarMap.put("shield", "🛡️");
            avatarMap.put("bow", "🏹");
            avatarMap.put("sword", "🗡️");
            avatarMap.put("crystal", "🔮");
            avatarMap.put("lion", "🦁");
            avatarMap.put("lightning", "⚡");
            avatarMap.put("star", "🌟");
            avatarMap.put("dragon", "🐉");

            avatarMap.forEach((name, icon) -> {
                if (!avatarRepository.existsByIconName(name)) {
                    Avatar avatar = new Avatar();
                    avatar.setIcon(icon);
                    avatar.setIconName(name);

                    switch (icon) {
                        case "👤" -> {
                            avatar.setPrice(0);
                            avatar.setTitle("Pessoa");
                        }
                        case "🧙" -> {
                            avatar.setPrice(100);
                            avatar.setTitle("Mago Sábio");
                        }
                        case "👑" -> {
                            avatar.setPrice(100);
                            avatar.setTitle("Coroa Real");
                        }
                        case "⚔️" -> {
                            avatar.setPrice(150);
                            avatar.setTitle("Cavaleiro");
                        }
                        case "🛡️" -> {
                            avatar.setPrice(150);
                            avatar.setTitle("Escudeiro");
                        }
                        case "🏹" -> {
                            avatar.setPrice(200);
                            avatar.setTitle("Arqueiro");
                        }
                        case "🗡️" -> {
                            avatar.setPrice(200);
                            avatar.setTitle("Espadachim");
                        }
                        case "🔮" -> {
                            avatar.setPrice(250);
                            avatar.setTitle("Místico");
                        }
                        case "🦁" -> {
                            avatar.setPrice(250);
                            avatar.setTitle("Domador de Leões");
                        }
                        case "⚡" -> {
                            avatar.setPrice(300);
                            avatar.setTitle("Trovão");
                        }
                        case "🌟" -> {
                            avatar.setPrice(350);
                            avatar.setTitle("Estelar");
                        }
                        case "🐉" -> {
                            avatar.setPrice(500);
                            avatar.setTitle("Caçador de Dragões");
                        }
                        default -> {
                            avatar.setPrice(0);
                            avatar.setTitle("Desconhecido");
                        }
                    }

                    avatar.setIconName(name);
                    avatarRepository.save(avatar);
                }
            });
            System.out.println("Avatares criados com sucesso.");
        }
    }

    @Transactional
    protected void createInitialAdmin() {
        final String ADMIN_EMAIL = "admin@runetasks.com";

        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {

            // Senha inicial temporária
            String initialPassword = "GigaPowerMasterSuperMegaBlaster123456*";

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: ROLE_ADMIN não encontrado."));

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: ROLE_USER não encontrado."));

            Avatar initialAvatar = avatarRepository.findByIconName("person")
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: Avatar inicial não encontrado."));

            List<Avatar> adminAvatars = avatarRepository.findAll();

            User admin = new User();
            admin.setEmail(ADMIN_EMAIL);
            admin.setNickname("AdminRuneTasks");
            admin.setName("Administrador Inicial");
            admin.setPassword(passwordEncoder.encode(initialPassword));
            admin.setCurrentAvatar(initialAvatar);
            admin.setOwnedAvatars(new HashSet<>(Collections.asSet(adminAvatars)));

            Set<Role> roles = new HashSet<>(Arrays.asList(adminRole, userRole));
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("Usuário Admin inicial criado com sucesso: " + ADMIN_EMAIL);
        }
    }

    private void createAdminSkill() {
        User adminUser = userRepository.findByEmail("admin@runetasks.com")
                .orElseThrow(() -> new ResourceNotFoundException("Erro: ADM não encontrado."));

        if (!skillRepository.existsByUser(adminUser)) {
            final String SKILL_NAME = "Habilidade do ADM";

            Skill skill = new Skill();
            skill.setName(SKILL_NAME);
            skill.setIcon("personal");
            skill.setUser(adminUser);

            skillRepository.save(skill);
            System.out.println("Habilidade de Admin inicial criada com sucesso: " + SKILL_NAME);
        }
    }

    private void createAdminTask() {
        User adminUser = userRepository.findByEmail("admin@runetasks.com")
                .orElseThrow(() -> new ResourceNotFoundException("Erro: ADM não encontrado."));

        List<Skill> adminSkills = skillRepository.findByUserId(adminUser.getId());

        if (adminSkills.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhuma habilidade de Admin inicial encontrada.");
        }

        Skill adminSkill = adminSkills.getFirst();

        if (!taskRepository.existsBySkill(adminSkill)) {
            final String TASK_TITLE = "Tarefa do ADM";

            Task task = new Task();
            task.setTitle(TASK_TITLE);
            task.setDescription("Descrição de template para tarefas criadas");
            task.setTaskXP(50);
            task.setUser(adminUser);
            task.setSkill(adminSkill);
            task.setDate(LocalDate.now());
            task.setRepeatType(RepeatType.NONE);

            taskRepository.save(task);
            System.out.println("Tarefa de Admin inicial criada com sucesso: " + TASK_TITLE);
        }
    }

    private void createAdminReward() {
        User adminUser = userRepository.findByEmail("admin@runetasks.com")
                .orElseThrow(() -> new ResourceNotFoundException("Erro: ADM não encontrado."));
        
        final String REWARD_TITLE = "Recompensa do ADM";

        if (rewardRepository.findByUserId(adminUser.getId()).isEmpty()) {
            Reward reward = new Reward();
            reward.setTitle(REWARD_TITLE);
            reward.setUser(adminUser);
            reward.setPrice(75);
            reward.setStatus(adminUser.getTotalCoins() >= 75 ? RewardStatus.AVAILABLE : RewardStatus.EXPENSIVE);
            
            rewardRepository.save(reward);
            System.out.println("Recompensa de Admin inicial criada com sucesso: " + REWARD_TITLE);
        }
    }
}
