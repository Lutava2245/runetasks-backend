package com.fatec.runetasks.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.request.RewardCreateRequest;
import com.fatec.runetasks.domain.dto.request.RewardUpdateRequest;
import com.fatec.runetasks.domain.dto.response.RewardResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.RewardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/rewards")
@Tag(name = "Recompensas", description = "Endpoints para gerenciamento de recompensas")
public class RewardController {

    private final RewardService rewardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas as recompensas", description = "Retorna uma lista de todas as recompensas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recompensas listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<RewardResponse>> getAllRewards() {
        List<RewardResponse> RewardResponses = rewardService.getAll();
        return ResponseEntity.ok(RewardResponses);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Listar recompensas por usuário", description = "Retorna uma lista de recompensas cadastradas por um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recompensas listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<RewardResponse>> getAllRewardsByUser(@PathVariable Long id) {
        List<RewardResponse> RewardResponses = rewardService.getByUserId(id);
        return ResponseEntity.ok(RewardResponses);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Buscar recompensa por ID", description = "Retorna os detalhes de uma recompensa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recompensa encontrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Recompensa não encontrada")
    })
    public ResponseEntity<RewardResponse> getReward(@PathVariable Long id) {
        RewardResponse RewardResponse = rewardService.getById(id);
        return ResponseEntity.ok(RewardResponse);
    }

    @PostMapping("register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cadastrar nova recompensa", description = "Cria uma nova recompensa associada ao usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recompensa criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Void> registerReward(@RequestBody RewardCreateRequest requestDTO,
            @AuthenticationPrincipal User user) {
        rewardService.createReward(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Editar recompensa", description = "Atualiza os dados de uma recompensa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recompensa atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Recompensa não encontrada")
    })
    public ResponseEntity<Void> editReward(@RequestBody RewardUpdateRequest requestDTO, @PathVariable Long id) {
        rewardService.updateRewardById(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Excluir recompensa", description = "Exclui uma recompensa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recompensa excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Recompensa não encontrada")
    })
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        rewardService.deleteRewardById(id);
        return ResponseEntity.noContent().build();
    }
}
