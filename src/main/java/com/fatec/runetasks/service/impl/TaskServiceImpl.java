package com.fatec.runetasks.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.runetasks.domain.dto.request.TaskCreateRequest;
import com.fatec.runetasks.domain.dto.request.TaskUpdateRequest;
import com.fatec.runetasks.domain.dto.response.TaskResponse;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RepeatType;
import com.fatec.runetasks.domain.model.enums.TaskStatus;
import com.fatec.runetasks.domain.repository.SkillRepository;
import com.fatec.runetasks.domain.repository.TaskRepository;
import com.fatec.runetasks.event.UserBalanceChangedEvent;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.LockedTaskException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.TaskService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final ApplicationEventPublisher eventPublisher;

    private final TaskRepository taskRepository;

    private final SkillRepository skillRepository;

    @Override
    public TaskResponse convertToDTO(Task task) {
        int coins = task.getTaskXP() / 2;

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getTaskXP(),
                coins,
                task.getSkill().getName(),
                task.getDate(),
                task.getRepeatType().name());
    }

    @Override
    public boolean isOwner(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        return task.getUser().getId().equals(userId);
    }

    @Override
    public boolean isFromSkill(Long taskId, Long skillId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        return task.getSkill().getId().equals(skillId);
    }

    @Override
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        return convertToDTO(task);
    }

    @Override
    public List<TaskResponse> getAll() {
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getByUserId(Long id) {
        List<Task> tasks = taskRepository.findByUserId(id);

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getBySkillId(Long id) {
        List<Task> tasks = taskRepository.findBySkillId(id);

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createTask(TaskCreateRequest request, User user) {
        Skill skill = skillRepository.findByNameAndUser(request.getSkillName(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Habilidade não encontrada."));

        int taskXP = switch (request.getDifficulty()) {
            case "medium" -> 30;
            case "hard" -> 50;
            default -> 20;
        };

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDate(request.getDate());
        task.setRepeatType(RepeatType.valueOf(request.getRepeatType().toUpperCase()));
        task.setTaskXP(taskXP);
        task.setUser(user);
        task.setSkill(skill);

        taskRepository.save(task);
    }

    @Transactional
    @Override
    public void updateTaskById(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        switch (task.getStatus()) {
            case BLOCKED -> throw new LockedTaskException("Erro: Tarefa está bloqueada.");
            case COMPLETED -> throw new DuplicateResourceException("Erro: Tarefa já foi completada.");
            default -> {
            }
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDate(request.getDate());
        task.setRepeatType(RepeatType.valueOf(request.getRepeatType().toUpperCase()));

        taskRepository.save(task);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    @Override
    public void resetRecurringTasks() {
        List<Task> recurringTasks = taskRepository.findByRepeatTypeNotAndDateBefore(RepeatType.NONE, LocalDate.now());

        for (Task task : recurringTasks) {
            if (task.getStatus().equals(TaskStatus.COMPLETED)) {
                task.prepareNextOccurrence();
            }
        }

        taskRepository.saveAll(recurringTasks);
    }

    @Transactional
    @Override
    public void toggleTaskBlock(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        if (task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new DuplicateResourceException("Erro: Tarefa já foi completada.");
        }

        boolean block = task.getStatus().equals(TaskStatus.BLOCKED);
        task.setStatus(block ? TaskStatus.PENDING : TaskStatus.BLOCKED);

        taskRepository.save(task);
    }

    @Transactional
    @Override
    public void markTaskAsComplete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        if (Objects.requireNonNull(task.getStatus()) == TaskStatus.COMPLETED) {
            throw new DuplicateResourceException("Erro: Tarefa já foi completada.");
        }

        User user = task.getUser();
        Skill skill = task.getSkill();

        task.setStatus(TaskStatus.COMPLETED);
        user.addXP(task.getTaskXP());
        user.addCoins(task.getTaskXP() / 2);
        skill.addXP(task.getTaskXP());

        eventPublisher.publishEvent(new UserBalanceChangedEvent(user));

        taskRepository.save(task);
    }

    @Transactional
    @Override
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        if (Objects.requireNonNull(task.getStatus()) == TaskStatus.BLOCKED) {
            throw new LockedTaskException("Erro: Tarefa está bloqueada.");
        }

        taskRepository.delete(task);
    }

}
