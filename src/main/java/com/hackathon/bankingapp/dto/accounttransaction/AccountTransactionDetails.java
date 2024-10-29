package com.hackathon.bankingapp.dto.accounttransaction;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransactionDetails {

    private Long id;
    private BigDecimal amount;
    private String transactionType;
    private long transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;

}