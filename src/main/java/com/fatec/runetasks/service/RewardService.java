package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.RewardCreateRequest;
import com.fatec.runetasks.domain.dto.request.RewardUpdateRequest;
import com.fatec.runetasks.domain.dto.response.RewardResponse;
import com.fatec.runetasks.domain.model.Reward;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.RewardStatus;
import com.fatec.runetasks.event.UserBalanceChangedEvent;
import com.fatec.runetasks.exception.ResourceNotFoundException;

/**
 * Interface de serviço para operações da entidade {@link Reward}.
 * <p>
 * Contém métodos para o gerenciamento de recompensas e seus estados, como
 * encontrar recompensas, excluir recompensas, editá-las, entre outros. Também
 * possui métodos auxiliares como conversão dos dados da entidade para DTO e
 * verificação de quem é o dono da recompensa.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface RewardService {

    /**
     * Converte uma recompensa para {@link RewardResponse}.
     * 
     * @param reward Recompensa a ser convertida.
     * @return a recompensa convertida para DTO.
     */
    RewardResponse convertToDTO(Reward reward);

    /**
     * Verifica se o usuário é dono da recompensa.
     * <p>
     * É utilizado para garantir que apenas o dono da recompensa possa utilizá-la.
     * <p>
     * 
     * @param rewardId Identificador único da recompensa.
     * @param userId   Identificador único do usuário.
     * @return {@code true} se o usuário for dono da recompensa,
     *         {@code false} caso contrário.
     * @throws ResourceNotFoundException Caso a recompensa não seja encontrada.
     */
    boolean isOwner(Long rewardId, Long userId);

    /**
     * Atualiza o status das recompensas do usuário de acordo com seu saldo.
     * <p>
     * Este método recebe um {@link UserBalanceChangedEvent}, alterando o
     * status das recompensas que não foram resgatadas
     * ({@link RewardStatus#REDEEMED}). Se ele possui moedas suficientes para
     * resgatá-las, o
     * status é definido como {@link RewardStatus#AVAILABLE}, caso contrário é
     * definido como {@link RewardStatus#EXPENSIVE}.
     * <p>
     * 
     * @param event Evento de mudança de saldo do usuário.
     */
    void handleBalanceChange(UserBalanceChangedEvent event);

    /**
     * Obtém uma recompensa pelo seu {@code id}.
     * 
     * @param id Identificador único da recompensa.
     * @return uma {@link RewardResponse} contendo a recompensa convertida para DTO.
     * @throws ResourceNotFoundException Caso a recompensa não seja encontrada.
     */
    RewardResponse getById(Long id);

    /**
     * Obtém uma lista contendo todas as recompensas registradas no sistema.
     * 
     * @return uma {@link List} de {@link RewardResponse} contendo as recompensas
     *         convertidas para DTO.
     */
    List<RewardResponse> getAll();

    /**
     * Obtém uma lista contendo todas as recompensas registradas pelo usuário.
     * 
     * @param id Identificador único do usuário.
     * @return uma {@link List} de {@link RewardResponse} contendo as recompensas
     *         convertidas para DTO.
     */
    List<RewardResponse> getByUserId(Long id);

    /**
     * Registra uma nova recompensa para o usuário.
     * <p>
     * O preço da recompensa é definido com base no nível do quanto o usuário gosta
     * desta recompensa:
     * <ul>
     * <li>Nível 1 -> 30 moedas</li>
     * <li>Nível 2 -> 50 moedas</li>
     * <li>Nível 3 -> 75 moedas</li>
     * <li>Nível 4 -> 100 moedas</li>
     * <li>Nível 5 -> 150 moedas</li>
     * </ul>
     * <p>
     * O status da recompensa é definido como {@link RewardStatus#AVAILABLE} se o
     * usuário
     * possuir moedas suficientes para comprá-la, caso contrário, é definido como
     * {@link RewardStatus#EXPENSIVE}.
     * <p>
     * 
     * @param request Requisição contendo os dados da recompensa.
     * @param user    Usuário que está criando a recompensa.
     * @see RewardStatus
     */
    void createReward(RewardCreateRequest request, User user);

    /**
     * Atualiza os dados de uma recompensa pelo seu {@code id}.
     * 
     * @param id      Identificador único da recompensa.
     * @param request Requisição contendo os dados da recompensa.
     * @throws ResourceNotFoundException Caso a recompensa não seja encontrado.
     */
    void updateRewardById(Long id, RewardUpdateRequest request);

    /**
     * Exclui uma recompensa pelo seu {@code id}.
     * 
     * @param id Identificador único da recompensa.
     * @throws ResourceNotFoundException Caso a recompensa não seja encontrada.
     */
    void deleteRewardById(Long id);

}
