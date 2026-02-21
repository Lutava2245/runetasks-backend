package com.fatec.runetasks.domain.model;

import java.time.LocalDate;

import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.domain.model.enums.TaskDifficulty;
import com.fatec.runetasks.domain.model.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TaskDifficulty difficulty;

    @Column
    private LocalDate date = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    public int getTaskXp() {
        return switch (this.difficulty) {
            case EASY -> 20;
            case MEDIUM -> 40;
            case HARD -> 60;
        };
    }

    public int getTaskCoins() {
        return switch (this.difficulty) {
            case EASY -> 5;
            case MEDIUM -> 15;
            case HARD -> 25;
        };
    }

    public void prepareNextOccurrence() {
        if (this.repeatType == RepeatType.NONE) {
            return;
        }

        this.date = switch (this.repeatType) {
            case DAILY -> this.date.plusDays(1);
            case WEEKLY -> this.date.plusWeeks(1);
            case MONTHLY -> this.date.plusMonths(1);
            default -> this.date;
        };

        this.status = TaskStatus.PENDING;
    }
}
