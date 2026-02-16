package com.fatec.runetasks.controller;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/tasks")
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista de todas as tarefas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> taskResponses = taskService.getAll();
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("skill/{id}")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Listar tarefas por habilidade", description = "Retorna tarefas associadas a uma habilidade específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
    })
    public ResponseEntity<List<TaskResponse>> getAllTasksBySkill(@PathVariable Long id) {
        List<TaskResponse> taskResponses = taskService.getBySkillId(id);
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Listar tarefas por usuário", description = "Retorna tarefas de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
    })
    public ResponseEntity<List<TaskResponse>> getAllTasksByUser(@PathVariable Long id) {
        List<TaskResponse> taskResponses = taskService.getByUserId(id);
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna os detalhes de uma tarefa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
    })
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse taskResponse = taskService.getById(id);
        return ResponseEntity.ok(taskResponse);
    }

    @PostMapping("register")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwnerByName(#a0.getSkillName(), principal.id)")
    @Operation(summary = "Cadastrar nova tarefa", description = "Cria uma nova tarefa associada a um usuário e habilidade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Habilidade não encontrada")
    })
    public ResponseEntity<Void> registerTask(@RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal User user) {
        taskService.createTask(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Editar tarefa", description = "Atualiza os dados de uma tarefa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Tarefa já foi completada"),
            @ApiResponse(responseCode = "412", description = "Tarefa bloqueada")
    })
    public ResponseEntity<Void> editTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        taskService.updateTaskById(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/block")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Bloquear/Desbloquear tarefa", description = "Alterna o status de bloqueio de uma tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa bloqueada/desbloqueada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Tarefa já foi completada")
    })
    public ResponseEntity<Void> blockTask(@PathVariable Long id) {
        taskService.toggleTaskBlock(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Concluir tarefa", description = "Marca uma tarefa como concluída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa concluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Tarefa já foi completada")
    })
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        taskService.markTaskAsComplete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Excluir tarefa", description = "Exclui uma tarefa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "412", description = "Tarefa bloqueada")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

}
