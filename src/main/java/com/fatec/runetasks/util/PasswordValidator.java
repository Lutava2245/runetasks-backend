package com.fatec.runetasks.util;

import org.springframework.stereotype.Component;

import com.fatec.runetasks.exception.WeakPasswordException;

@Component
public class PasswordValidator {

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
    public void verifyStrength(String password) {
        if (password.length() < 8) {
            throw new WeakPasswordException();
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        if (!hasUppercase || !hasLowercase || !hasDigit) {
            throw new WeakPasswordException();
        }
    }

}
