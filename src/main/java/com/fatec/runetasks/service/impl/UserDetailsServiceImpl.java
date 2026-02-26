package com.fatec.runetasks.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Classe de serviço que implementa a interface {@link UserDetailsService} para
 * carregar os detalhes do usuário para autenticação.
 * <p>
 * O método {@link #loadUserByUsername} é implementado para buscar um usuário
 * pelo email ou nickname, utilizando o repositório {@link UserRepository}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     * 
     * @throws UsernameNotFoundException Caso o usuário não for encontrado com o
     *                                   {@code identifier}.
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) {
        return userRepository.findByEmailOrNickname(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com o identificador: " + identifier));
    }

}
