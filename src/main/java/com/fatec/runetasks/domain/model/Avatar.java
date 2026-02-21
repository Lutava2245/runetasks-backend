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
 * Representa um avatar que pode ser comprado pelo {@link User}.
 * <p>
 * Um avatar é uma imagem que pode ser usada como foto de perfil.
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
     * Ícone do avatar.
     */
    @Column(nullable = false, unique = true)
    private String icon;

    /**
     * Preço do avatar.
     */
    @Column(nullable = false)
    private int price;

}
