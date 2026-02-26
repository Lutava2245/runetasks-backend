package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.SkillRequest;
import com.fatec.runetasks.domain.dto.response.SkillResponse;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.ResourceNotFoundException;

/**
 * Interface de serviço para operações da entidade {@link Skill}.
 * <p>
 * Contém métodos para o gerenciamento de habilidades e seus estados, como
 * encontrar habilidades, excluir tarefas, editá-las, entre outros. Também
 * possui métodos auxiliares como conversão dos dados da entidade para DTO e
 * verificação de quem é o dono da habilidade.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface SkillService {

    /**
     * Converte uma habilidade para {@link SkillResponse}.
     * <p>
     * O processo calcula o percentual de nível atual da habilidade e conta o
     * número de tarefas associadas a ela.
     * <p>
     * 
     * @param skill Habilidade a ser convertida.
     * @return a habilidade convertida para DTO.
     */
    SkillResponse convertToDTO(Skill skill);

    /**
     * Verifica se o usuário é dono da habilidade.
     * <p>
     * É utilizado para garantir que apenas o dono da habilidade possa utilizá-la.
     * <p>
     * 
     * @param skillId Identificador único da habilidade.
     * @param userId  Identificador único do usuário.
     * @return {@code true} se o usuário for dono da habilidade,
     *         {@code false} caso contrário.
     * @throws ResourceNotFoundException Caso a habilidade não seja encontrada.
     */
    boolean isOwner(Long skillId, Long userId);

    /**
     * Obtém uma habilidade pelo seu {@code id}.
     * 
     * @param id Identificador único da habilidade.
     * @return uma {@link SkillResponse} contendo a habilidade convertida para DTO.
     * @throws ResourceNotFoundException Caso a habilidade não seja encontrada.
     */
    SkillResponse getById(Long id);

    /**
     * Obtém uma lista contendo todas as habilidades registradas no sistema.
     * 
     * @return uma {@link List} de {@link SkillResponse} contendo as habilidades
     *         convertidas para DTO.
     */
    List<SkillResponse> getAll();

    /**
     * Obtém uma lista contendo todas as habilidades registradas pelo usuário.
     * 
     * @param id Identificador único do usuário.
     * @return uma {@link List} de {@link SkillResponse} contendo as habilidades
     *         convertidas para DTO.
     */
    List<SkillResponse> getByUserId(Long id);

    /**
     * Registra uma nova habilidade para o usuário.
     * 
     * @param request Requisição contendo os dados da habilidade.
     * @param user    Usuário que está criando a habilidade.
     * @throws DuplicateResourceException Caso uma habilidade de mesmo nome já
     *                                    exista entre as habilidades do usuário.
     */
    void createSkill(SkillRequest request, User user);

    /**
     * Atualiza os dados de uma habilidade pelo {@code id}.
     * 
     * @param id      Identificador único da habilidade.
     * @param request Requisição contendo os dados da habilidade.
     * @throws ResourceNotFoundException  Caso a habilidade não seja encontrada.
     * @throws DuplicateResourceException Caso uma habilidade de mesmo nome já
     *                                    exista entre as habilidades do usuário.
     */
    void updateSkillById(Long id, SkillRequest request);

    /**
     * Exclui uma habilidade pelo seu {@code id}.
     * 
     * @param id Identificador único da habilidade.
     * @throws ResourceNotFoundException Caso a habilidade não seja encontrada.
     */
    void deleteSkillById(Long id);

}
