package com.fatec.runetasks.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "skills")
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column
    private int totalXP = 0;

    @Column
    private int level = 1;

    @Column
    private int progressXP;

    @Column
    private int xpToNextLevel = 90;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
