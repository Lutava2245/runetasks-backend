package com.fatec.runetasks.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.domain.model.enums.TaskDifficulty;
import com.fatec.runetasks.domain.model.enums.TaskStatus;
import com.fatec.runetasks.domain.repository.TaskRepository;
import com.fatec.runetasks.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    @DisplayName("Deve resetar tarefas recorrentes diariamente")
    void resetRecurringTasks_Daily() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Task task = new Task();
        task.setDate(yesterday);
        task.setRepeatType(RepeatType.DAILY);
        task.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByRepeatTypeNotAndDateBefore(any(), any())).thenReturn(List.of(task));

        taskService.resetRecurringTasks();

        assertEquals(LocalDate.now(), task.getDate());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        verify(taskRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve resetar tarefas recorrentes semanalmente")
    void resetRecurringTasks_Weekly() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        Task task = new Task();
        task.setDate(lastWeek);
        task.setRepeatType(RepeatType.WEEKLY);
        task.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByRepeatTypeNotAndDateBefore(any(), any())).thenReturn(List.of(task));

        taskService.resetRecurringTasks();

        assertEquals(LocalDate.now(), task.getDate());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        verify(taskRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve resetar tarefas recorrentes mensalmente")
    void resetRecurringTasks_Monthly() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        Task task = new Task();
        task.setDate(lastMonth);
        task.setRepeatType(RepeatType.MONTHLY);
        task.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByRepeatTypeNotAndDateBefore(any(), any())).thenReturn(List.of(task));

        taskService.resetRecurringTasks();

        assertEquals(LocalDate.now(), task.getDate());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        verify(taskRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve ignorar tarefas que não são recorrentes")
    void resetRecurringTasks_NotReset() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Task task = new Task();
        task.setDate(yesterday);
        task.setRepeatType(RepeatType.NONE);
        task.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByRepeatTypeNotAndDateBefore(any(), any())).thenReturn(List.of(task));

        taskService.resetRecurringTasks();

        assertEquals(yesterday, task.getDate());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    @DisplayName("Deve subir o nível do usuário quando o Xp ultrapassar o limite")
    void markTaskAsComplete_UserLevel() {
        User user = new User();
        user.setProgressXp(100);

        Task task = new Task();
        task.setDifficulty(TaskDifficulty.MEDIUM);
        task.setUser(user);
        task.setSkill(new Skill());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.markTaskAsComplete(1L);

        assertEquals(2, user.getLevel());
        assertEquals(20, user.getProgressXp());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    @DisplayName("Deve subir o nível da habilidade quando o XP ultrapassar o limite")
    void markTaskAsComplete_SkillLevel() {
        Skill skill = new Skill();

        Task task = new Task();
        task.setDifficulty(TaskDifficulty.MEDIUM);
        task.setUser(new User());
        task.setSkill(skill);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.markTaskAsComplete(1L);

        assertEquals(2, skill.getLevel());
        assertEquals(10, skill.getProgressXp());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção se a tarefa já estiver completa")
    void markTaskAsComplete_CompletedTask() {
        Task task = new Task();
        task.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(DuplicateResourceException.class, () -> taskService.markTaskAsComplete(1L));
    }
}