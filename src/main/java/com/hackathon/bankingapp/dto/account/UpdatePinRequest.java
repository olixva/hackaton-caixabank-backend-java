package com.hackathon.bankingapp.dto.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePinRequest {

    @NotBlank(message = "Old pin is required")
    private String oldPin;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "New pin is required")
    private String newPin;

}