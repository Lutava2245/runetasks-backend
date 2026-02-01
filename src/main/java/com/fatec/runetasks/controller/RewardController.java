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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.runetasks.domain.dto.request.RewardRequest;
import com.fatec.runetasks.domain.dto.response.RewardResponse;
import com.fatec.runetasks.domain.model.User;
import com.fatec.runetasks.service.RewardService;

@RestController
@CrossOrigin(origins ="*", maxAge = 3600)
@RequestMapping("api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RewardResponse>> getAllRewards() {
        List<RewardResponse> RewardResponses = rewardService.getAll();
        return ResponseEntity.ok(RewardResponses);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<List<RewardResponse>> getAllRewardsByUser(@PathVariable Long id) {
        List<RewardResponse> RewardResponses = rewardService.getByUserId(id);
        return ResponseEntity.ok(RewardResponses);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<RewardResponse> getReward(@PathVariable Long id) {
        RewardResponse RewardResponse = rewardService.getById(id);
        return ResponseEntity.ok(RewardResponse);
    }
    
    @PostMapping("register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> registerReward(@RequestBody RewardRequest requestDTO, @AuthenticationPrincipal User user) {
        rewardService.createReward(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<Void> editReward(@RequestBody RewardRequest requestDTO, @PathVariable Long id) {
        rewardService.updateRewardById(id, requestDTO);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @rewardServiceImpl.isOwner(#id, principal.id)")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        rewardService.deleteRewardById(id);
        return  ResponseEntity.noContent().build();
    }
}
