package com.hackathon.bankingapp.dto.assets;

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
public class BuyAssetsRequest {

    @NotBlank(message = "Asset symbol is required")
    private String assetSymbol;

    @NotNull(message = "Pin is required")
    private String pin;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

}