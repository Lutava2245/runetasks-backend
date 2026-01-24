package com.fatec.runetasks.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.LockedTaskException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SkillRepository skillRepository;

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

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhuma tarefa encontrada.");
        }

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getByUserId(Long id) {
        List<Task> tasks = taskRepository.findByUserId(id);

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhuma tarefa encontrada.");
        }

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getBySkillId(Long id) {
        List<Task> tasks = taskRepository.findBySkillId(id);

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("Erro: Nenhuma tarefa encontrada.");
        }

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
            case COMPLETED -> throw new LockedTaskException("Erro: Tarefa já foi completada.");
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
    public void resetRecurringTasks() {
        LocalDate now = LocalDate.now();

        List<Task> recurringTasks = taskRepository
                .findByStatusAndRepeatTypeNot(TaskStatus.COMPLETED, RepeatType.NONE);

        for (Task task : recurringTasks) {
            if (task.getDate().isBefore(now)) {
                switch (task.getRepeatType()) {
                    case DAILY -> task.setDate(task.getDate().plusDays(1));
                    case WEEKLY -> task.setDate(task.getDate().plusWeeks(1));
                    case MONTHLY -> task.setDate(task.getDate().plusMonths(1));
                    default -> task.getDate();
                }
                task.setStatus(TaskStatus.PENDING);

                taskRepository.save(task);
            }
        }
    }

    @Transactional
    @Override
    public void toggleTaskBlock(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        if (task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new LockedTaskException("Erro: Tarefa já foi completada.");
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
        User user = task.getUser();
        Skill skill = task.getSkill();

        switch (task.getStatus()) {
            case COMPLETED -> throw new LockedTaskException("Erro: Tarefa já foi completada.");
            default -> {
            }
        }

        int taskXP = task.getTaskXP();
        int taskCoins = taskXP / 2;

        task.setStatus(TaskStatus.COMPLETED);

        user.setTotalXP(user.getTotalXP() + taskXP);
        user.setProgressXP(user.getProgressXP() + taskXP);
        user.setTotalCoins(user.getTotalCoins() + taskCoins);
        if (user.getProgressXP() >= user.getXpToNextLevel()) {
            if (user.getProgressXP() > user.getXpToNextLevel()) {
                user.setProgressXP(user.getProgressXP() - user.getXpToNextLevel());
            } else {
                user.setProgressXP(0);
            }
            user.setLevel(user.getLevel() + 1);
            user.setXpToNextLevel(user.getXpToNextLevel() + (30 * user.getLevel()));
        }

        skill.setTotalXP(skill.getTotalXP() + taskXP);
        skill.setProgressXP(skill.getProgressXP() + taskXP);
        if (skill.getProgressXP() >= skill.getXpToNextLevel()) {
            if (skill.getProgressXP() > skill.getXpToNextLevel()) {
                skill.setProgressXP(skill.getProgressXP() - skill.getXpToNextLevel());
            } else {
                skill.setProgressXP(0);
            }
            skill.setLevel(skill.getLevel() + 1);
            skill.setXpToNextLevel(skill.getXpToNextLevel() + (20 * skill.getLevel()));
        }

        userRepository.save(user);
        skillRepository.save(skill);
        taskRepository.save(task);
    }

    @Transactional
    @Override
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Tarefa não encontrada"));

        switch (task.getStatus()) {
            case BLOCKED -> throw new LockedTaskException("Erro: Tarefa está bloqueada.");
            default -> {
            }
        }

        taskRepository.delete(task);
    }

}
