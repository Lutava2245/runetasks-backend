package com.fatec.runetasks.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa um avatar que pode ser comprado e usado por um {@link User}.
 * <p>
 * Um avatar é um ícone que pode ser comprado utilizando moedas e usado como
 * foto de perfil de usuários.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "avatars")
public class Avatar {

    /**
     * Identificador único do avatar.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título do avatar.
     */
    @Column(nullable = false, length = 50, unique = true)
    private String title;

    /**
     * Nome do ícone do avatar.
     */
    @Column(nullable = false, unique = true)
    private String iconName;

    /**
     * Preço do avatar.
     */
    @Column(nullable = false)
    private int price;

}
