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

import com.fatec.runetasks.domain.dto.request.SkillRequest;
import com.fatec.runetasks.domain.dto.response.SkillResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/skills")
@Tag(name = "Habilidades", description = "Endpoints para gerenciamento de habilidades")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas as habilidades", description = "Retorna uma lista de todas as habilidades cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habilidades listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        List<SkillResponse> skillResponses = skillService.getAll();
        return ResponseEntity.ok(skillResponses);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Listar habilidades por usuário", description = "Retorna habilidades de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habilidades listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<SkillResponse>> getAllSkillsByUser(@PathVariable Long id) {
        List<SkillResponse> skillResponses = skillService.getByUserId(id);
        return ResponseEntity.ok(skillResponses);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Buscar habilidade por ID", description = "Retorna os detalhes de uma habilidade específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habilidade encontrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Habilidade não encontrada")
    })
    public ResponseEntity<SkillResponse> getSkill(@PathVariable Long id) {
        SkillResponse skillResponse = skillService.getById(id);
        return ResponseEntity.ok(skillResponse);
    }

    @PostMapping("register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cadastrar nova habilidade", description = "Cria uma nova habilidade associada a um usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Habilidade criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "409", description = "Habilidade já existente")
    })
    public ResponseEntity<Void> registerSkill(@RequestBody SkillRequest requestDTO,
            @AuthenticationPrincipal User user) {
        skillService.createSkill(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Editar habilidade", description = "Atualiza os dados de uma habilidade existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Habilidade atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Habilidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "Habilidade já existente")
    })
    public ResponseEntity<Void> editSkill(@RequestBody SkillRequest requestDTO, @PathVariable Long id) {
        skillService.updateSkillById(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @skillServiceImpl.isOwner(#id, principal.id)")
    @Operation(summary = "Excluir habilidade", description = "Exclui uma habilidade existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Habilidade excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Habilidade não encontrada")
    })
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkillById(id);
        return ResponseEntity.noContent().build();
    }

}
