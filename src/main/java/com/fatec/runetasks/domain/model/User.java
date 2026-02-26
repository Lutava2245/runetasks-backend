package com.fatec.runetasks.domain.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.InsufficientCoinsException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa um usuário do sistema.
 * <p>
 * Um usuário é uma pessoa que pode criar tarefas, recompensas e habilidades,
 * utilizar avatares, ganhar moedas e subir de nível.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do usuário.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Nickname do usuário.
     */
    @Column(nullable = false, length = 50, unique = true)
    private String nickname;

    /**
     * Senha do usuário.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Email do usuário.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Avatar atual do usuário.
     */
    @ManyToOne
    @JoinColumn(name = "avatar_id", nullable = false)
    private Avatar currentAvatar;

    /**
     * Total de pontos de experiência do usuário.
     */
    @Column
    private int totalXp = 0;

    /**
     * Total de moedas do usuário.
     */
    @Column
    private int totalCoins = 0;

    /**
     * Nível do usuário.
     */
    @Column
    private int level = 1;

    /**
     * Pontos de experiência do usuário.
     */
    @Column
    private int progressXp = 0;

    /**
     * Data de criação do usuário.
     */
    @Column
    private LocalDate createdAt = LocalDate.now();

    /**
     * Avatares que o usuário possui.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_avatars", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "avatar_id"))
    private Set<Avatar> ownedAvatars;

    /**
     * Papéis do usuário.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    /**
     * Retorna as autoridades do usuário derivadas dos seus papéis.
     * 
     * @return Coleção de autoridades do usuário.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna o {@code username} do usuário utilizado para autenticação.
     * <p>
     * O {@code username} pode ser o e-mail do usuário ou seu nickname. Este método
     * retorna o email.
     * <p>
     * 
     * @return o email do usuário.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Retorna o total de XP necessário para atingir o pŕoximo nível.
     * <p>
     * O XP necessário é calculado de acordo com o nível atual do usuário.
     * <p>
     * 
     * @return um <code>int</code> do total de XP para subir de nível
     */
    public int getXpToNextLevel() {
        return 30 + (90 * level);
    }

    /**
     * Adiciona pontos de experiência ao usuário.
     * <p>
     * Quando o usuário atinge o XP necessário, os pontos de experiência são
     * resetados e o seu nível sobe.
     * <p>
     * 
     * @param xp Pontos de experiência a serem adicionados.
     */
    public void addXp(final int xp) {
        this.totalXp += xp;
        this.progressXp += xp;

        while (this.progressXp >= getXpToNextLevel()) {
            this.progressXp -= getXpToNextLevel();
            this.level++;
        }
    }

    /**
     * Adiciona moedas ganhas ao usuário.
     * 
     * @param amount Moedas a serem adicionadas.
     */
    public void addCoins(final int amount) {
        this.totalCoins += amount;
    }

    /**
     * Remove uma quantidade de moedas do usuário.
     * 
     * @param amount o {@code int} de moedas a serem removidas.
     * @throws InsufficientCoinsException Caso o usuário não tenha moedas
     *                                    suficientes.
     */
    public void spendCoins(final int amount) {
        if (this.totalCoins < amount) {
            throw new InsufficientCoinsException();
        }
        this.totalCoins -= amount;
    }

    /**
     * Verifica se o usuário possui um avatar.
     * 
     * @param avatar Avatar a ser verificado.
     * @return {@code true} se o usuário possui o avatar,
     *         {@code false} caso contrário.
     */
    public boolean hasAvatar(final Avatar avatar) {
        return this.ownedAvatars.stream()
                .anyMatch(a -> a.getIconName().equals(avatar.getIconName()));
    }

    /**
     * Adiciona um novo avatar para a lista de avatares do usuário.
     * 
     * @param avatar Avatar a ser adicionado.
     * @throws DuplicateResourceException Caso o usuário já possua o avatar.
     */
    public void addAvatar(final Avatar avatar) {
        if (hasAvatar(avatar)) {
            throw new DuplicateResourceException("Você já possui este avatar.");
        }
        this.ownedAvatars.add(avatar);
    }

}
