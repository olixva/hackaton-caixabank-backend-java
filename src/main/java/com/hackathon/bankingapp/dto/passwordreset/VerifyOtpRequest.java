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
public class VerifyOtpRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Otp is required")
    private String otp;

}