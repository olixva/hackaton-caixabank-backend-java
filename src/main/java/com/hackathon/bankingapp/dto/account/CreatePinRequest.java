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
public class CreatePinRequest {

    @NotBlank(message = "Pin is required")
    private String pin;

    @NotBlank(message = "Password is required")
    private String password;

}