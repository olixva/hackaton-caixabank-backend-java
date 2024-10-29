package com.hackathon.bankingapp.dto.passwordreset;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Reset Token is required")
    private String resetToken;

    @NotBlank(message = "New Password is required")
    private String newPassword;

}