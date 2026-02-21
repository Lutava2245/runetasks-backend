package com.fatec.runetasks.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um papel que pode ser atribuído a um {@link User}.
 * <p>
 * Um papel é um conjunto de permissões que determinam o que um usuário pode
 * fazer no sistema.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Identificador único do papel.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do papel.
     */
    @Column(nullable = false, unique = true)
    private String name;

}
