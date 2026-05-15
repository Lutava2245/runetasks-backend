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
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.RewardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar endpoints relacionados a entidade {@link Reward}.
 * <p>
 * Fornece endpoints para operações CRUD como criação, leitura, atualização e
 * exclusão de recompensas. A maioria dos endpoints requer autenticação, e
 * alguns são restritos a usuários com o {@link Role} de administrador.
 * <p>
 * 
 * @author Luan T. Felix
 * @see RewardService
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/rewards")
@Tag(name = "Recompensas", description = "Endpoints para gerenciamento de recompensas")
public class RewardController {

    private final RewardService rewardService;

    /**
     * Endpoint para listar todas as recompensas cadastradas no sistema.
     * <p>
     * Este endpoint é restrito a usuários com o papel de administrador.
     * <p>
     * 
     * @return um {@link ResponseEntity} contendo a lista de todas as
     *         {@link RewardResponse} ou mensagem de erro.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas as recompensas", description = "Retorna uma lista de todas as recompensas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recompensas listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<RewardResponse>> getAllRewards() {
        List<RewardResponse> rewardResponses = rewardService.getAll();
        return ResponseEntity.ok(rewardResponses);
    }

    /**
     * Endpoint para listar todas as recompensas associadas a um usuário específico.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário dono
     * das recompensas.
     * <p>
     * 
     * @param id Identificador único do usuário dono das recompensas.
     * @return um {@link ResponseEntity} contendo a lista de {@link RewardResponse}
     *         do usuário ou mensagem de erro.
     */
    @GetMapping("user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Listar recompensas por usuário", description = "Retorna uma lista de recompensas cadastradas por um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recompensas listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<RewardResponse>> getAllRewardsByUser(@PathVariable Long id) {
        List<RewardResponse> rewardResponses = rewardService.getByUserId(id);
        return ResponseEntity.ok(rewardResponses);
    }

    /**
     * Endpoint para buscar uma recompensa pelo seu {@code id} no banco de dados.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário dono
     * da recompensa.
     * <p>
     * 
     * @param id Identificador único da recompensa.
     * @return um {@link ResponseEntity} contendo a {@link RewardResponse} ou
     *         mensagem de erro.
     */
    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Buscar recompensa por ID", description = "Retorna os detalhes de uma recompensa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recompensa encontrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Recompensa não encontrada")
    })
    public ResponseEntity<RewardResponse> getReward(@PathVariable Long id) {
        RewardResponse rewardResponse = rewardService.getById(id);
        return ResponseEntity.ok(rewardResponse);
    }

    /**
     * Endpoint para registrar uma recompensa no banco de dados.
     * <p>
     * Este endpoint é acessível para qualquer usuário autenticado no sistema.
     * <p>
     * 
     * @param requestDTO Requisição contendo os dados da recompensa.
     * @param user       Usuário autenticado.
     * @return um {@link ResponseEntity} com status {@code 201 Created} se a
     *         recompensa for criada com sucesso ou mensagem de erro.
     */
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

    /**
     * Endpoint para editar os dados de uma recompensa existente.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário dono
     * da recompensa.
     * <p>
     * 
     * @param requestDTO Requisição contendo os dados atualizados da recompensa.
     * @param id         Identificador único da recompensa a ser editada.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se a
     *         recompensa for atualizada com sucesso ou mensagem de erro.
     */
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

    /**
     * Endpoint para deletar uma recompensa existente.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário dono
     * da recompensa.
     * <p>
     * 
     * @param id Identificador único da recompensa a ser excluída.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se a
     *         recompensa for excluída ou mensagem de erro.
     */
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
