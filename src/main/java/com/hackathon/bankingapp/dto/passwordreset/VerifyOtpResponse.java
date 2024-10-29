package com.hackathon.bankingapp.dto.passwordreset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpResponse {

    private String passwordResetToken;

}