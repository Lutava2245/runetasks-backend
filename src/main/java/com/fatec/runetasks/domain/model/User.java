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

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "avatar_id", nullable = false)
    private Avatar currentAvatar;

    @Column
    private int totalXp = 0;

    @Column
    private int totalCoins = 0;

    @Column
    private int level = 1;

    @Column
    private int progressXp = 0;

    @Column
    private LocalDate createdAt = LocalDate.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_avatars", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "avatar_id"))
    private Set<Avatar> ownedAvatars;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public int getXpToNextLevel() {
        return 30 + (90 * level);
    }

    public void addXp(final int xp) {
        this.totalXp += xp;
        this.progressXp += xp;

        while (this.progressXp >= getXpToNextLevel()) {
            this.progressXp -= getXpToNextLevel();
            this.level++;
        }
    }

    public void addCoins(final int amount) {
        this.totalCoins += amount;
    }

    public void spendCoins(final int amount) {
        if (this.totalCoins < amount) {
            throw new InsufficientCoinsException();
        }
        this.totalCoins -= amount;
    }

    public boolean hasAvatar(final Avatar avatar) {
        return this.ownedAvatars.stream()
                .anyMatch(a -> a.getIcon().equals(avatar.getIcon()));
    }

    public void addAvatar(final Avatar avatar) {
        if (hasAvatar(avatar)) {
            throw new DuplicateResourceException("Você já possui este avatar.");
        }
        this.ownedAvatars.add(avatar);
    }

}
