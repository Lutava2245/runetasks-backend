package com.fatec.runetasks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.request.ChangePasswordRequest;
import com.fatec.runetasks.domain.dto.request.UserCreateRequest;
import com.fatec.runetasks.domain.dto.request.UserUpdateRequest;
import com.fatec.runetasks.domain.dto.response.UserResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.UserService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAll();
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse userResponse = userService.getById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal User authenticatedUser) {
        UserResponse userResponse = userService.convertToDTO(authenticatedUser);
        return ResponseEntity.ok(userResponse);
    }
    
    @PostMapping("register")
    public ResponseEntity<Void> registerUser(@RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<Void> editUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUserById(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("avatar/{avatarName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> selectAvatar(@AuthenticationPrincipal User user, @PathVariable String avatarName) {
        userService.selectAvatar(user, avatarName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
