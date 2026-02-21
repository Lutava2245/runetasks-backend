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
    @DisplayName("Deve resetar data de vencimento de tarefas recorrentes")
    void resetRecurringTasks_Reset() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Task dailyTask = new Task();
        dailyTask.setDate(yesterday);
        dailyTask.setRepeatType(RepeatType.DAILY);
        dailyTask.setStatus(TaskStatus.COMPLETED);

        Task weeklyTask = new Task();
        weeklyTask.setDate(yesterday);
        weeklyTask.setRepeatType(RepeatType.WEEKLY);
        weeklyTask.setStatus(TaskStatus.COMPLETED);

        Task monthlyTask = new Task();
        monthlyTask.setDate(yesterday);
        monthlyTask.setRepeatType(RepeatType.MONTHLY);
        monthlyTask.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByRepeatTypeNotAndDateBefore(any(), any())).thenReturn(List.of(dailyTask, weeklyTask, monthlyTask));

        taskService.resetRecurringTasks();

        assertEquals(LocalDate.now(), dailyTask.getDate());
        assertEquals(TaskStatus.PENDING, dailyTask.getStatus());

        assertEquals(yesterday.plusWeeks(1), weeklyTask.getDate());
        assertEquals(TaskStatus.PENDING, weeklyTask.getStatus());

        assertEquals(yesterday.plusMonths(1), monthlyTask.getDate());
        assertEquals(TaskStatus.PENDING, monthlyTask.getStatus());
        verify(taskRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve resetar tarefas recorrentes atrasadas para a próxima data")
    void resetRecurringTasks_Overdue() {
        LocalDate overdueDaily = LocalDate.now().minusDays(3);
        LocalDate overdueWeekly = LocalDate.now().minusWeeks(2).minusDays(3);
        LocalDate overdueMonthly = LocalDate.now().minusMonths(1).minusWeeks(2).minusDays(3);

        Task dailyTask = new Task();
        dailyTask.setDate(overdueDaily);
        dailyTask.setRepeatType(RepeatType.DAILY);
        dailyTask.setStatus(TaskStatus.COMPLETED);

        Task weeklyTask = new Task();
        weeklyTask.setDate(overdueWeekly);
        weeklyTask.setRepeatType(RepeatType.WEEKLY);
        weeklyTask.setStatus(TaskStatus.COMPLETED);

        Task monthlyTask = new Task();
        monthlyTask.setDate(overdueMonthly);
        monthlyTask.setRepeatType(RepeatType.MONTHLY);
        monthlyTask.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByRepeatTypeNotAndDateBefore(any(), any())).thenReturn(List.of(dailyTask, weeklyTask, monthlyTask));

        taskService.resetRecurringTasks();

        assertEquals(LocalDate.now(), dailyTask.getDate());
        assertEquals(TaskStatus.PENDING, dailyTask.getStatus());

        assertEquals(overdueWeekly.plusWeeks(3), weeklyTask.getDate());
        assertEquals(TaskStatus.PENDING, weeklyTask.getStatus());

        assertEquals(overdueMonthly.plusMonths(2), monthlyTask.getDate());
        assertEquals(TaskStatus.PENDING, monthlyTask.getStatus());
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
    @DisplayName("Deve subir o nível do usuário e da habilidade quando o XP ultrapassar o limite")
    void markTaskAsComplete_Level() {
        User user = new User();
        user.setProgressXp(100);

        Skill skill = new Skill();

        Task task = new Task();
        task.setDifficulty(TaskDifficulty.MEDIUM);
        task.setUser(user);
        task.setSkill(skill);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.markTaskAsComplete(1L);

        assertEquals(2, user.getLevel());
        assertEquals(20, user.getProgressXp());

        assertEquals(2, skill.getLevel());
        assertEquals(10, skill.getProgressXp());
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