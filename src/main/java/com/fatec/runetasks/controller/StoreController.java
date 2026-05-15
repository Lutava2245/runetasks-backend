package com.fatec.runetasks.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.response.AvatarResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.AvatarService;
import com.fatec.runetasks.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar endpoints relacionados ao sistema de loja.
 * <p>
 * Fornece endpoints para operações gastos de moedas com {@link Reward} e
 * {@link Avatar}, além da busca por items para a loja.
 * <p>
 * 
 * @author Luan T. Felix
 * @see StoreService
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/store")
@Tag(name = "Loja", description = "Endpoints relacionados ao sistema de loja")
public class StoreController {

    private final AvatarService avatarService;

    private final StoreService storeService;

    /**
     * Endpoint para listar todos os avatares da loja para o usuário autenticado.
     * <p>
     * Este endpoint é acessível para qualquer usuário autenticado no sistema.
     * <p>
     * 
     * @param user Usuário autenticado.
     * @return um {@link ResponseEntity} contendo a lista de todos os
     *         {@link AvatarResponse} ou mensagem de erro.
     */
    @GetMapping("avatars")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos os avatares", description = "Retorna uma lista de todos os avatares da loja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatares listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<AvatarResponse>> getAllAvatars(@AuthenticationPrincipal User user) {
        List<AvatarResponse> avatarsResponse = avatarService.getAll(user);
        return ResponseEntity.ok(avatarsResponse);
    }

    /**
     * Endpoint para comprar um avatar da loja.
     * <p>
     * Este endpoint é acessível para qualquer usuário autenticado no sistema.
     * <p>
     * 
     * @param user     Usuário autenticado.
     * @param avatarId Identificador único do avatar a ser comprado.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se o
     *         avatar for comprado com sucesso ou mensagem de erro.
     */
    @PatchMapping("buy/avatar/{avatarId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Comprar avatar", description = "Desbloqueia um avatar para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Avatar comprado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Avatar não encontrado"),
            @ApiResponse(responseCode = "409", description = "Avatar já comprado"),
            @ApiResponse(responseCode = "412", description = "Saldo insuficiente")
    })
    public ResponseEntity<Void> buyAvatar(@AuthenticationPrincipal User user, @PathVariable Long avatarId) {
        storeService.buyAvatar(user, avatarId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para resgatar uma recompensa da loja.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário dono
     * da recompensa.
     * <p>
     * 
     * @param rewardId Identificador único da recompensa a ser resgatada.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se a
     *         recompensa for resgatada com sucesso ou mensagem de erro.
     */
    @PatchMapping("buy/reward/{rewardId}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#rewardId, principal.id)")
    @Operation(summary = "Reivindicar recompensa", description = "Reivindica uma recompensa para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recompensa reivindicada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Recompensa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Recompensa já reivindicada"),
            @ApiResponse(responseCode = "412", description = "Saldo insuficiente")
    })
    public ResponseEntity<Void> claimReward(@PathVariable Long rewardId) {
        storeService.claimReward(rewardId);
        return ResponseEntity.noContent().build();
    }

}
