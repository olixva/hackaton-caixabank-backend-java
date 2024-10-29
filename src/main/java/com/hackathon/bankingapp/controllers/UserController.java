package com.hackathon.bankingapp.controllers;

import com.hackathon.bankingapp.dto.user.LoginResponse;
import com.hackathon.bankingapp.dto.user.LoginRequest;
import com.hackathon.bankingapp.dto.user.RegisterRequest;
import com.hackathon.bankingapp.dto.user.UserInfoResponse;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.service.AuthService;
import com.hackathon.bankingapp.service.JwtService;
import com.hackathon.bankingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to users")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(
            summary = "Login a user",
            description = "Returns a JWT token if the user is found in the database"
    )
    @PostMapping(value = "users/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(
            summary = "Register a user",
            description = "Returns the user details if the user is successfully registered"
    )
    @PostMapping(value = "users/register")
    public ResponseEntity<UserInfoResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    @Operation(
            summary = "Logout a user",
            description = "Logs out the user by invalidating the JWT token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "users/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get user details",
            description = "Returns the user details for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "dashboard/user")
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserInfo(user));
    }
}
