package com.fatec.runetasks.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.fatec.runetasks.domain.dto.request.ForgotPasswordRequest;
import com.fatec.runetasks.domain.dto.request.LoginRequest;
import com.fatec.runetasks.domain.dto.response.LoginResponse;
import com.fatec.runetasks.domain.model.PasswordToken;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.domain.repository.PasswordTokenRepository;
import com.fatec.runetasks.domain.repository.UserRepository;
import com.fatec.runetasks.service.AuthService;
import com.fatec.runetasks.util.EmailHelper;
import com.fatec.runetasks.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${app.frontend.url}")
    private String frontendURL;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final EmailHelper emailHelper;

    private final UserRepository userRepository;

    private final PasswordTokenRepository passwordTokenRepository;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails);
        return new LoginResponse(jwt);
    }

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

}
