package com.fatec.runetasks.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.runetasks.exception.WeakPasswordException;

@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {
    
    @InjectMocks
    private PasswordValidator passwordValidator;

    @Test
    @DisplayName("Deve lançar exceção se a senha não for forte o suficiente.")
    void verifyPasswordStrength_Weak() {
        String weakPassword = "fraco";

        assertThrows(WeakPasswordException.class, () -> passwordValidator.verifyStrength(weakPassword));
    }

}
