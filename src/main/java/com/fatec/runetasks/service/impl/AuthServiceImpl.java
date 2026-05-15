package com.fatec.runetasks.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.fatec.runetasks.exception.InvalidPasswordException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import com.fatec.runetasks.domain.dto.request.ForgotPasswordRequest;
import com.fatec.runetasks.domain.dto.request.LoginRequest;
import com.fatec.runetasks.domain.dto.request.ResetPasswordRequest;
import com.fatec.runetasks.domain.dto.response.LoginResponse;
import com.fatec.runetasks.domain.model.PasswordToken;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.PasswordTokenRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.exception.ResourceNotFoundException;
import com.fatec.runetasks.exception.SamePasswordException;
import com.fatec.runetasks.exception.TokenExpiredException;
import com.fatec.runetasks.service.AuthService;
import com.fatec.runetasks.util.EmailHelper;
import com.fatec.runetasks.util.JwtUtil;
import com.fatec.runetasks.util.PasswordValidator;

import lombok.RequiredArgsConstructor;

/**
 * Classe de serviço para operações de autenticação e gerenciamento de senhas de
 * {@link User}.
 * <p>
 * Contém métodos para autenticar usuários, iniciar o processo de redefinição de
 * senha e para redefinir a senha.
 * <p>
 * Esta classe é uma implementação concreta da interface {@link AuthService}.
 * <p>
 * 
 * @author Luan T. Felix
 */
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${app.frontend.url}")
    private String frontendURL;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final PasswordValidator passwordValidator;

    private final PasswordEncoder passwordEncoder;

    private final EmailHelper emailHelper;

    private final UserRepository userRepository;

    private final PasswordTokenRepository passwordTokenRepository;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmailOrNickname(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Usuário não encontrado."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails);
        return new LoginResponse(jwt);
    }

    @Transactional
    @Override
    public void initiatePasswordReset(ForgotPasswordRequest resquest) {
        Optional<User> userOptional = userRepository.findByEmail(resquest.getEmail());

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        if (passwordTokenRepository.existsByUser(user)) {
            passwordTokenRepository.deleteByUser(user);
        }

        String token = UUID.randomUUID().toString();

        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setUser(user);
        passwordToken.setToken(token);
        passwordToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        passwordTokenRepository.save(passwordToken);

        String link = frontendURL + "/reset-password?tk=" + passwordToken.getToken();

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("link", link);

        emailHelper.sendHtmlMessage(
                user.getEmail(),
                "Redefinição de Senha - RuneTasks",
                "mail/reset-password",
                context);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(request.getResetToken())
                .orElseThrow(() -> new ResourceNotFoundException("Erro: Token não encontrado"));

        if (passwordToken.isExpired()) {
            passwordTokenRepository.delete(passwordToken);
            throw new TokenExpiredException();
        }

        User user = passwordToken.getUser();

        passwordValidator.verifyStrength(request.getNewPassword());

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordTokenRepository.delete(passwordToken);
    }

}
