package com.fatec.runetasks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.dto.request.UserCreateRequest;
import com.fatec.runetasks.domain.dto.request.UserUpdateRequest;
import com.fatec.runetasks.domain.dto.response.UserResponse;
import com.fatec.runetasks.domain.model.User;

@Service
public interface UserService {

    UserResponse convertToDTO(User user);

    void verifyPasswordStrength(String password);

    UserResponse getById(Long id);

    List<UserResponse> getAll();

    void createUser(UserCreateRequest request);

    void updateUserById(Long id, UserUpdateRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void selectAvatar(User user, String avatarName);

    void deleteUserById(Long id);

}
