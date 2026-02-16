package com.fatec.runetasks.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.InvalidPasswordException;
import com.fatec.runetasks.exception.SamePasswordException;
import com.fatec.runetasks.exception.WeakPasswordException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Deve lançar exceção se a senha não for forte o suficiente.")
    void verifyPasswordStrength_Weak() {
        String weakPassword = "fraco";

        assertThrows(WeakPasswordException.class, () -> userService.verifyPasswordStrength(weakPassword));
    }

    @Test
    @DisplayName("Deve lançar exceção se a senha atual não estiver correta.")
    void changePassword_WrongPassword() {
        User user = new User();
        user.setPassword(passwordEncoder.encode("AntigoPassword123"));
        String wrongPassword = "PasswordErrado123";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(wrongPassword);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
                .thenReturn(request.getCurrentPassword().equals(user.getPassword()));

        assertThrows(InvalidPasswordException.class, () -> userService.changePassword(1L, request));
    }

    @Test
    @DisplayName("Deve lançar exceção se a nova senha for idêntica a atual.")
    void changePassword_SamePassword() {
        User user = new User();
        user.setPassword("AntigoPassword123");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(user.getPassword());
        request.setNewPassword(user.getPassword());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
                .thenReturn(request.getCurrentPassword().equals(user.getPassword()));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
                .thenReturn(request.getNewPassword().equals(user.getPassword()));

        assertThrows(SamePasswordException.class, () -> userService.changePassword(1L, request));
    }

    @Test
    @DisplayName("Deve trocar senha do usuário corretamente.")
    void changePassword_Success() {
        User user = new User();
        user.setPassword("AntigoPassword123");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(user.getPassword());
        request.setNewPassword("NovoPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn(request.getNewPassword());
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
                .thenReturn(request.getCurrentPassword().equals(user.getPassword()));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
                .thenReturn(request.getNewPassword().equals(user.getPassword()));

        userService.changePassword(1L, request);

        assertEquals(request.getNewPassword(), user.getPassword());
    }

}
