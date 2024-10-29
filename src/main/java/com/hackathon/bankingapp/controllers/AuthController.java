package com.hackathon.bankingapp.controllers;

import com.hackathon.bankingapp.dto.passwordreset.ResetPasswordRequest;
import com.hackathon.bankingapp.dto.passwordreset.SendOtpRequest;
import com.hackathon.bankingapp.dto.MessageResponse;
import com.hackathon.bankingapp.dto.passwordreset.VerifyOtpRequest;
import com.hackathon.bankingapp.dto.passwordreset.VerifyOtpResponse;
import com.hackathon.bankingapp.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Operations related to password reset")
public class AuthController {

    private final PasswordResetService passwordResetService;

    @Operation(
            summary = "Request password reset OTP",
            description = "Sends an email to the user with a OTP to reset the password"
    )
    @PostMapping(value = "/send-otp")
    public ResponseEntity<MessageResponse> sendOtp(@Valid @RequestBody SendOtpRequest sendOtpRequest) {
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .message("OTP sent successfully to: " + passwordResetService.sendOtp(sendOtpRequest.getIdentifier()))
                        .build()
        );
    }

    @Operation(
            summary = "Verify OTP",
            description = "Verifies the OTP sent to the user, and returns a password reset token"
    )
    @PostMapping(value = "/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        return ResponseEntity.ok(
                VerifyOtpResponse.builder()
                        .passwordResetToken(passwordResetService.verifyOtp(verifyOtpRequest.getIdentifier(), verifyOtpRequest.getOtp()))
                        .build()
        );
    }

    @Operation(
            summary = "Reset Password",
            description = "Resets the password for the user, using the password reset token"
    )
    @PostMapping()
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        passwordResetService.resetPassword(resetPasswordRequest.getIdentifier(), resetPasswordRequest.getResetToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .message("Password reset successfully")
                        .build()
        );
    }
}