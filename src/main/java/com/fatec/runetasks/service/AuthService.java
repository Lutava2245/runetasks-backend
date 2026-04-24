package com.fatec.runetasks.service;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.ForgotPasswordRequest;
import com.fatec.runetasks.domain.dto.request.LoginRequest;
import com.fatec.runetasks.domain.dto.response.LoginResponse;

@Service
public interface AuthService {

    LoginResponse authenticate(LoginRequest request);

    void initiatePasswordReset(ForgotPasswordRequest request);

}
