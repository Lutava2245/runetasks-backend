package com.fatec.runetasks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.request.TaskCreateRequest;
import com.fatec.runetasks.domain.dto.request.TaskUpdateRequest;
import com.fatec.runetasks.domain.dto.response.TaskResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.TaskService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> taskResponses = taskService.getAll();
        return ResponseEntity.ok(taskResponses);
    }
    
    @GetMapping("skill/{id}")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<List<TaskResponse>> getAllTasksBySkill(@PathVariable Long id) {
        List<TaskResponse> taskResponses = taskService.getBySkillId(id);
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<List<TaskResponse>> getAllTasksByUser(@PathVariable Long id) {
        List<TaskResponse> taskResponses = taskService.getByUserId(id);
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getById(id);
        return ResponseEntity.ok(taskResponse);
    }

    @PostMapping("register")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwnerByName(#a0.getSkillName(), principal.id)")
    public ResponseEntity<Void> registerTask(@RequestBody TaskCreateRequest request, @AuthenticationPrincipal User user) {
        taskService.createTask(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<Void> editTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        taskService.updateTaskById(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/block")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<Void> blockTask(@PathVariable Long id) {
        taskService.toggleTaskBlock(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        taskService.markTaskAsComplete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

}
