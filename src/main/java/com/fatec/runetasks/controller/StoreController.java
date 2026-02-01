package com.fatec.runetasks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.response.AvatarResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.AvatarService;
import com.fatec.runetasks.service.StoreService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/store")
public class StoreController {

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private StoreService storeService;

    @GetMapping("avatars")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AvatarResponse>> getAllAvatars(@AuthenticationPrincipal User user) {
        List<AvatarResponse> avatarsResponse = avatarService.getAllAvatars(user);
        return ResponseEntity.ok(avatarsResponse);
    }

    @PatchMapping("buy/avatar/{avatarId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> buyAvatar(@AuthenticationPrincipal User user, @PathVariable Long avatarId) {
        storeService.buyAvatar(user, avatarId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("buy/reward/{rewardId}")
    @PreAuthorize("isAuthenticated() || @rewardServiceImpl.isOwner(#rewardId, principal.id)")
    public ResponseEntity<Void> claimReward(@AuthenticationPrincipal User user, @PathVariable Long rewardId) {
        storeService.claimReward(rewardId);
        return ResponseEntity.noContent().build();
    }

}
