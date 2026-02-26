package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.dto.request.UserCreateRequest;
import com.fatec.runetasks.domain.dto.request.UserUpdateRequest;
import com.fatec.runetasks.domain.dto.response.UserResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.Role;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.exception.DuplicateResourceException;
import com.fatec.runetasks.exception.InvalidPasswordException;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.exception.SamePasswordException;
import com.fatec.runetasks.exception.WeakPasswordException;

/**
 * Interface de serviço para operações da entidade {@link User}.
 * <p>
 * Contém métodos para o gerenciamento de usuários e seus estados, como
 * encontrar usuários, trocar senhas, registrá-los, entre outros. Também possui
 * métodos auxiliares como conversão dos dados da entidade para DTO e validação
 * de senhas.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface UserService {

    /**
     * Converte um usuário para {@link UserResponse}
     * <p>
     * O processo calcula o percentual do nível atual do usuário e verifica a
     * quantidade de itens disponíveis na loja de acordo com seu total de moedas.
     * <p>
     * 
     * @param user Usuário a ser convertido.
     * @return o usuário convertido para DTO.
     */
    UserResponse convertToDTO(User user);

    /**
     * Verifica a força da senha recebida.
     * <p>
     * O processo verifica se {@code password} possui pelo menos 8 caracteres, uma
     * letra maiúscula, uma letra minúscula e um número.
     * <p>
     * 
     * @param password uma {@link String} a ser verificada.
     * @throws WeakPasswordException Caso a senha não seja forte o suficiente.
     */
    void verifyPasswordStrength(String password);

    /**
     * Obtém um usuário pelo seu {@code id}.
     * 
     * @param id Identificador único da tarefa.
     * @return uma {@link UserResponse} contendo o usuário convertido para DTO.
     * @throws ResourceNotFoundException Caso o usuário não seja encontrado.
     */
    UserResponse getById(Long id);

    /**
     * Obtém uma lista contendo todos os usuários cadastrados no sistema.
     * 
     * @return uma {@link List} de {@link UserResponse} contendo os usuários
     *         convertidas para DTO.
     */
    List<UserResponse> getAll();

    /**
     * Registra um novo usuário no sistema.
     * <p>
     * O processo criptografa a senha recebida, define um {@link Avatar} inicial e
     * um {@link Role} padrão para o seu cadastro.
     * <p>
     * 
     * @param request Requisição contendo os dados do usuário.
     * @throws DuplicateResourceException Caso o email ou nickname já estejam
     *                                    cadastrados.
     * @throws WeakPasswordException      Caso a senha não seja forte o suficiente.
     * @throws ResourceNotFoundException  Caso o role ou o avatar não sejam
     *                                    encontrados.
     * @see #verifyPasswordStrength(String)
     */
    void createUser(UserCreateRequest request);

    /**
     * Atualiza os dados de um usuário pelo seu {@code id}.
     * 
     * @param id      Identificador único do usuário.
     * @param request Requisição contendo os dados do usuário.
     * @throws ResourceNotFoundException Caso o usuário não seja encontrado.
     */
    void updateUserById(Long id, UserUpdateRequest request);

    /**
     * Altera a senha do usuário pelo seu {@code id}.
     * <p>
     * O processo verifica se a senha atual do usuário é igual à nova senha e se a
     * nova senha é forte o suficiente. Se a senha for válida, ela é criptografada.
     * <p>
     * 
     * @param id      Identificador único do usuário.
     * @param request Requisição com a senha atual e a nova senha.
     * @throws ResourceNotFoundException Caso o usuário não seja encontrado.
     * @throws InvalidPasswordException  Caso a senha atual seja inválida.
     * @throws SamePasswordException     Caso a nova senha seja igual à senha atual.
     * @throws WeakPasswordException     Caso a nova senha não seja forte o
     *                                   suficiente.
     * @see #verifyPasswordStrength(String)
     */
    void changePassword(Long id, ChangePasswordRequest request);

    /**
     * Seleciona um avatar para o usuário.
     * <p>
     * O processo busca o {@link Avatar} a ser selecionado na lista de avatares que
     * o {@link User} possui. Se o avatar for encontrado, ele é definido como o
     * avatar atual.
     * <p>
     * 
     * @param user       Usuário que está selecionando o avatar.
     * @param avatarName Nome do avatar a ser selecionado.
     * @throws ResourceNotFoundException Caso o avatar não seja encontrado ou o
     *                                   usuário não possua o avatar.
     */
    void selectAvatar(User user, String avatarName);

    /**
     * Exclui um usuário pelo seu {@code id}.
     * 
     * @param id Identificador único do usuário.
     * @throws ResourceNotFoundException Caso o usuário não seja encontrado.
     */
    void deleteUserById(Long id);

}
