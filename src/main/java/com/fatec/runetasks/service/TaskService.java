package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.TaskCreateRequest;
import com.fatec.runetasks.domain.dto.request.TaskUpdateRequest;
import com.fatec.runetasks.domain.dto.response.TaskResponse;
import com.fatec.runetasks.domain.model.Skill;
import com.fatec.runetasks.domain.model.Task;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.model.enums.TaskDifficulty;
import com.fatec.runetasks.domain.model.enums.TaskStatus;
import com.fatec.runetasks.event.UserBalanceChangedEvent;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.LockedTaskException;
import com.fatec.runetasks.exception.ResourceNotFoundException;

/**
 * Interface de serviço para operações da entidade {@link Task}.
 * <p>
 * Contém métodos para o gerenciamento de tarefas e seus estados, como
 * encontrar tarefas, editá-las, completar e bloquear, entre outros. Também
 * possui métodos auxiliares como conversão dos dados da entidade para DTO,
 * verificação de quem é o dono da recompensa e de qual é a habilidade
 * relacionada.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface TaskService {

    /**
     * Converte uma tarefa para {@link TaskResponse}.
     * 
     * @param task Tarefa a ser convertida.
     * @return a tarefa convertida para DTO.
     */
    TaskResponse convertToDTO(Task task);

    /**
     * Verifica se o usuário é dono da tarefa.
     * <p>
     * É utilizado para garantir que apenas o dono da tarefa possa utilizá-la.
     * <p>
     * 
     * @param taskId Identificador único da tarefa.
     * @param userId Identificador único do usuário.
     * @return {@code true} se o usuário for dono da tarefa,
     *         {@code false} caso contrário.
     * @throws ResourceNotFoundException Caso a tarefa não seja encontrada.
     */
    boolean isOwner(Long taskId, Long userId);

    /**
     * Verifica se a tarefa pertence à habilidade especificada.
     * <p>
     * É utilizado para garantir que apenas tarefas de uma habilidade específica
     * sejam utilizadas.
     * <p>
     * 
     * @param taskId  Identificador único da tarefa.
     * @param skillId Identificador único da habilidade.
     * @return {@code true} se a tarefa pertencer à habilidade,
     *         {@code false} caso contrário.
     * @throws ResourceNotFoundException Caso a tarefa não seja encontrada.
     */
    boolean isFromSkill(Long taskId, Long skillId);

    /**
     * Reseta o status de {@link Task} recorrentes já concluídas.
     * <p>
     * O processo é executado todos os dias à meia-noite verificando se há tarefas
     * recorrentes já completadas que precisam ter sua data de conclusão alterada.
     * <p>
     * 
     * @see Task#prepareNextOccurrence()
     */
    void resetRecurringTasks();

    /**
     * Obtém uma tarefa pelo seu {@code id}.
     * 
     * @param id Identificador único da tarefa.
     * @return uma {@link TaskResponse} contendo a tarefa convertida para DTO.
     * @throws ResourceNotFoundException Caso a tarefa não seja encontrada.
     */
    TaskResponse getById(Long id);

    /**
     * Obtém uma lista contendo todas as tarefas registradas no sistema.
     * 
     * @return uma {@link List} de {@link TaskResponse} contendo as tarefas
     *         convertidas para DTO.
     */
    List<TaskResponse> getAll();

    /**
     * Obtém uma lista contendo todas as tarefas registradas pelo usuário.
     * 
     * @param userId Identificador único do usuário.
     * @return uma {@link List} de {@link TaskResponse} contendo as tarefas
     *         convertidas para DTO.
     */
    List<TaskResponse> getByUserId(Long userId);

    /**
     * Obtém uma lista contendo todas as tarefas registradas em uma habilidade
     * específica.
     * 
     * @param skillId Identificador único da habilidade.
     * @return uma {@link List} de {@link TaskResponse} contendo as tarefas
     *         convertidas para DTO.
     */
    List<TaskResponse> getBySkillId(Long skillId);

    /**
     * Registra uma nova tarefa para o usuário.
     * 
     * @param request Requisição contendo os dados da tarefa.
     * @param user    Usuário que está criando a tarefa.
     * @throws ResourceNotFoundException Caso a habilidade que será atrelada não
     *                                   seja encontrada.
     */
    void createTask(TaskCreateRequest request, User user);

    /**
     * Atualiza os dados de uma tarefa pelo seu {@code id}.
     * <p>
     * A operação é restrita pelo status da tarefa: tarefas bloqueadas ou já
     * completadas não permitem modificações.
     * </p>
     *
     * @param id      Identificador único da tarefa.
     * @param request Requisição contendo os dados da tarefa.
     * @throws ResourceNotFoundException  Caso a habilidade que será atrelada não
     *                                    seja encontrada.
     * @throws LockedTaskException        Caso a tarefa estiver bloqueada
     *                                    ({@link TaskStatus#BLOCKED}).
     * @throws DuplicateResourceException Caso a tarefa já esteja concluída
     *                                    ({@link TaskStatus#COMPLETED}).
     */
    void updateTaskById(Long id, TaskUpdateRequest request);

    /**
     * Alterna o bloqueio de uma tarefa pelo seu {@code id}.
     * <p>
     * O processo define o status de tarefas pendentes ({@link TaskStatus#PENDING})
     * para bloqueada ({@link TaskStatus#BLOCKED}), e vice-versa.
     * <p>
     * Tarefas bloqueadas não podem ser editadas ou excluídas.
     * <p>
     * 
     * @param id Identificador único da tarefa.
     * @throws ResourceNotFoundException  Caso a tarefa não seja encontrada.
     * @throws DuplicateResourceException Caso a tarefa já esteja concluída
     *                                    ({@link TaskStatus#COMPLETED}).
     */
    void toggleTaskBlock(Long id);

    /**
     * Completa uma tarefa pelo seu {@code id}.
     * <p>
     * Ao completar a tarefa, o {@link User} recebe XP e moedas, e a {@link Skill}
     * atrelada recebe XP. Tanto o XP quanto as moedas da tarefa são definidas de
     * acordo com sua {@link TaskDifficulty}.
     * <p>
     * O status da tarefa é marcado como {@link TaskStatus#COMPLETED} e o sistema
     * emite um {@link UserBalanceChangedEvent} que atualiza o status das
     * recompensas do usuário.
     * <p>
     * 
     * @param id Identificador único da tarefa.
     * @throws ResourceNotFoundException  Caso a tarefa não seja encontrada.
     * @throws DuplicateResourceException Caso a tarefa já esteja concluída
     *                                    ({@link TaskStatus#COMPLETED}).
     * @see RewardService#handleBalanceChange(UserBalanceChangedEvent)
     */
    void markTaskAsComplete(Long id);

    /**
     * Exclui uma tarefa pelo seu {@code id}.
     * 
     * @param id Identificador único da tarefa.
     * @throws ResourceNotFoundException Caso a tarefa não seja encontrada.
     * @throws LockedTaskException       Caso a tarefa estiver bloqueada
     *                                   ({@link TaskStatus#BLOCKED}).
     */
    void deleteTaskById(Long id);

}
