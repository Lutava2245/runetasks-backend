package com.fatec.runetasks.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.*;
import com.fatec.runetasks.exception.ResourceNotFoundException;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;

/**
 * DataLoader é uma classe de configuração que implementa CommandLineRunner para
 * carregar dados iniciais na aplicação.
 * <p>
 * Ele é responsável por criar um usuário administrador inicial, caso ele ainda
 * não exista, com base nas credenciais configuradas no arquivo
 * application.properties. O método run é executado automaticamente na
 * inicialização da aplicação, e o método createAdmin é responsável por
 * verificar se o usuário administrador já existe e, se não existir, criar um
 * novo usuário administrador com as permissões adequadas.
 * <p>
 * O DataLoader só é ativado quando o perfil "dev" está ativo, garantindo que os
 * dados de teste sejam carregados apenas em ambientes de desenvolvimento.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Configuration
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final AvatarRepository avatarRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * O email do usuário administrador inicial, lido do arquivo de configuração da
     * aplicação (application.properties).
     */
    @Value("${app.admin.email}")
    private String adminEmail;

    /**
     * O password do usuário administrador inicial, lido do arquivo de configuração
     * da aplicação (application.properties).
     */
    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        createAdmin();
    }

    /**
     * Cria um usuário administrador inicial, caso ele ainda não exista.
     * <p>
     * O método verifica se um usuário com o email configurado já existe. Se não
     * existir, ele busca a role {@code ROLE_ADMIN} e o avatar inicial
     * {@code person}, e cria um novo usuário administrador com essas permissões e
     * avatares. O usuário é então salvo no banco de dados.
     * <p>
     */
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
