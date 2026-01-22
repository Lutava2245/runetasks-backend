package com.fatec.runetasks.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        return userRepository.findByEmailOrNickname(identifier, identifier)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuário não encontrado com o identificador: " + identifier
            ));
    }
    
}
