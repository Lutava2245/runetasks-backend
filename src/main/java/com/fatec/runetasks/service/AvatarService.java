package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.response.AvatarResponse;
import com.fatec.runetasks.domain.model.Avatar;
import com.fatec.runetasks.domain.model.User;

/**
 * Interface de serviço para operações da entidade {@link Avatar}.
 * <p>
 * Contém métodos para encontrar avatares na loja e para converter dos dados da
 * entidade para DTO.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Service
public interface AvatarService {

    /**
     * Converte um avatar para {@link AvatarResponse}.
     * <p>
     * O processo verifica se o usuário possui o avatar e retorna
     * {@code true} ou {@code false}.
     * <p>
     * 
     * @param avatar Avatar a ser convertido.
     * @param user   Usuário logado.
     * @return o avatar convertido para DTO.
     */
    AvatarResponse convertToDTO(Avatar avatar, User user);

    /**
     * Obtém uma lista contendo todos os avatares disponíveis para o usuário.
     * 
     * @param user Usuário logado.
     * @return uma {@link List} de {@link AvatarResponse} contendo todos os avatares
     *         convertidos para DTO.
     */
    List<AvatarResponse> getAll(User user);

}
