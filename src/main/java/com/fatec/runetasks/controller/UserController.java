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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.dto.request.UserCreateRequest;
import com.fatec.runetasks.domain.dto.request.UserUpdateRequest;
import com.fatec.runetasks.domain.dto.response.UserResponse;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar endpoints relacionados a entidade {@link User}.
 * <p>
 * Fornece endpoints para operações CRUD, bem como para a seleção de avatares e
 * alteração de senha. A maioria dos endpoints requer autenticação, e alguns são
 * restritos a usuários com o {@link Role} de administrador.
 * <p>
 * 
 * @author Luan T. Felix
 * @see UserService
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para listar todos os usuários cadastrados no sistema.
     * <p>
     * Este endpoint é restrito a usuários com o papel de administrador.
     * <p>
     * 
     * @return um {@link ResponseEntity} contendo a lista de todos os
     *         {@link UserResponse} ou mensagem de erro.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAll();
        return ResponseEntity.ok(userResponses);
    }

    /**
     * Endpoint para buscar um usuário pelo seu {@code id} no banco de dados.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário.
     * <p>
     * 
     * @param id Identificador único do usuário.
     * @return um {@link ResponseEntity} contendo o {@link UserResponse} ou
     *         mensagem de erro.
     */
    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse userResponse = userService.getById(id);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Endpoint para buscar o usuário atual autenticado no sistema.
     * <p>
     * Este endpoint é acessível para qualquer usuário autenticado no sistema.
     * <p>
     * 
     * @param user Usuário autenticado.
     * @return um {@link ResponseEntity} contendo o {@link UserResponse} ou
     *         mensagem de erro.
     */
    @GetMapping("profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar perfil do usuário autenticado", description = "Retorna os detalhes do perfil do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal User authenticatedUser) {
        UserResponse userResponse = userService.convertToDTO(authenticatedUser);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Endpoint para cadastrar um usuário no banco de dados.
     * <p>
     * Este endpoint não requer privilégios de administrador nem autenticação,
     * permitindo que novos usuários se registrem no sistema.
     * <p>
     * 
     * @param requestDTO Requisição contendo os dados do usuário.
     * @return um {@link ResponseEntity} com status {@code 201 Created} se o
     *         usuário for cadastrado com sucesso ou mensagem de erro.
     */
    @PostMapping("register")
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Role/Avatar não encontrado"),
            @ApiResponse(responseCode = "409", description = "Username já cadastrado")
    })
    public ResponseEntity<Void> registerUser(@RequestBody UserCreateRequest requestDTO) {
        userService.createUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para editar os dados de um usuário existente.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário.
     * <p>
     * 
     * @param requestDTO Requisição contendo os dados atualizados do usuário.
     * @param id         Identificador único do usuário a ser editado.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se o
     *         usuário for atualizado com sucesso ou mensagem de erro.
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Editar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário editado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> editUser(@RequestBody UserUpdateRequest requestDTO, @PathVariable Long id) {
        userService.updateUserById(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para alterar a senha de um usuário existente.
     * <p>
     * Este endpoint é acessível para administradores ou para o próprio usuário.
     * <p>
     * 
     * @param requestDTO Requisição contendo os dados para a alteração da senha do
     *                   usuário.
     * @param id         Identificador único do usuário.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se a
     *         senha for alterada com sucesso ou mensagem de erro.
     */
    @PatchMapping("{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Alterar senha do usuário", description = "Altera a senha de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Nova senha é idêntica a anterior")
    })
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest requestDTO, @PathVariable Long id) {
        userService.changePassword(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 
     * @param user
     * @param avatarName
     * @return
     */
    @PatchMapping("avatar/{avatarName}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Selecionar avatar", description = "Seleciona um dos avatares disponíveis para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Avatar selecionado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Avatar não encontrado")
    })
    public ResponseEntity<Void> selectAvatar(@AuthenticationPrincipal User user, @PathVariable String avatarName) {
        userService.selectAvatar(user, avatarName);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para deletar um usuário existente.
     * <p>
     * Este endpoint é restrito a usuários com o papel de administrador.
     * <p>
     * 
     * @param id Identificador único do usuário a ser excluído.
     * @return um {@link ResponseEntity} com status {@code 204 No Content} se o
     *         usuário for excluído ou mensagem de erro.
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
