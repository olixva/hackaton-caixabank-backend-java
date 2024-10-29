package com.hackathon.bankingapp.controllers;

import com.hackathon.bankingapp.dto.useractions.EnableAutoInvestRequest;
import com.hackathon.bankingapp.dto.useractions.SuscriptionRequest;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user-actions")
@RequiredArgsConstructor
@Tag(name = "User Actions", description = "Operations related to user actions")
public class UserActionsController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Subscribe to a service",
            description = "Subscribes to a service, and returns if the subscription was successful",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @PostMapping(value = "subscribe")
    public ResponseEntity<String> subscribe(@AuthenticationPrincipal User user, @Valid @RequestBody SuscriptionRequest suscriptionRequest) {
        subscriptionService.subscribe(user, suscriptionRequest.getPin(), suscriptionRequest.getAmount(), suscriptionRequest.getIntervalSeconds());
        return ResponseEntity.ok("Subscription created successfully.");
    }

    @Operation(
            summary = "Enable auto invest",
            description = "Enables auto invest bot, and returns if the auto invest was successful",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "enable-auto-invest")
    public ResponseEntity<String> enableAutoInvest(@AuthenticationPrincipal User user, @Valid @RequestBody EnableAutoInvestRequest enableAutoInvestRequest) {
        if (enableAutoInvestRequest.getPin() == null || enableAutoInvestRequest.getPin().isEmpty()) {
            return ResponseEntity.badRequest().body("PIN cannot be null or empty");
        }
        subscriptionService.enableAutoInvest(user, enableAutoInvestRequest.getPin());
        return ResponseEntity.ok("Automatic investment enabled successfully.");
    }
}