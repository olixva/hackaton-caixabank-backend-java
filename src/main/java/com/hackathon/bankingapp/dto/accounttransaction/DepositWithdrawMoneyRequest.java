package com.hackathon.bankingapp.dto.accounttransaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositWithdrawMoneyRequest {

    @NotBlank(message = "Pin is required")
    private String pin;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

}