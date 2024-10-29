package com.hackathon.bankingapp.service;

import com.hackathon.bankingapp.dto.accounttransaction.AccountTransactionDetails;
import com.hackathon.bankingapp.models.AccountTransaction;
import com.hackathon.bankingapp.models.TransactionType;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.repositories.AccountTransactionRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountTransactionRepository accountTransactionRepository;

    public void createDepositTransaction(User user, BigDecimal amount) {

        AccountTransaction transaction = AccountTransaction.builder()
                .amount(amount)
                .account(user.getAccount())
                .transactionType(TransactionType.CASH_DEPOSIT)
                .transactionDate(System.currentTimeMillis())
                .build();

        saveAccountTransaction(transaction);
    }

    public void createWithdrawTransaction(User user, BigDecimal amount) {

        AccountTransaction transaction = AccountTransaction.builder()
                .amount(amount)
                .account(user.getAccount())
                .transactionType(TransactionType.CASH_WITHDRAWAL)
                .transactionDate(System.currentTimeMillis())
                .build();

        saveAccountTransaction(transaction);
    }

    public void createTransferTransaction(User user, BigDecimal amount, UUID targetAccountNumber) {

        AccountTransaction transaction = AccountTransaction.builder()
                .amount(amount)
                .account(user.getAccount())
                .transactionType(TransactionType.CASH_TRANSFER)
                .transactionDate(System.currentTimeMillis())
                .targetAccountNumber(targetAccountNumber)
                .build();

        saveAccountTransaction(transaction);
    }

    public void createAssetPurchaseTransaction(User user, BigDecimal amount) {

        AccountTransaction transaction = AccountTransaction.builder()
                .amount(amount)
                .account(user.getAccount())
                .transactionType(TransactionType.ASSET_PURCHASE)
                .transactionDate(System.currentTimeMillis())
                .build();

        saveAccountTransaction(transaction);
    }

    public void createAssetSaleTransaction(User user, BigDecimal amount) {

        AccountTransaction transaction = AccountTransaction.builder()
                .amount(amount)
                .account(user.getAccount())
                .transactionType(TransactionType.ASSET_SELL)
                .transactionDate(System.currentTimeMillis())
                .build();

        saveAccountTransaction(transaction);
    }

    public void creaateSubscriptionTransaction(User user, BigDecimal amount) {

        AccountTransaction transaction = AccountTransaction.builder()
                .amount(amount)
                .account(user.getAccount())
                .transactionType(TransactionType.SUBSCRIPTION)
                .transactionDate(System.currentTimeMillis())
                .build();

        saveAccountTransaction(transaction);
    }

    public List<AccountTransactionDetails> getAccountTransactions(UUID accountNumber) {
        List<AccountTransaction> accountTransactions = accountTransactionRepository.findByAccountAccountNumber(accountNumber);
        List<AccountTransactionDetails> accountTransactionsDetails = new ArrayList<>();

        accountTransactions.forEach(transaction -> {
            AccountTransactionDetails accountTransactionDetails = AccountTransactionDetails.builder()
                    .id(transaction.getId())
                    .sourceAccountNumber(transaction.getAccount().getAccountNumber().toString())
                    .amount(transaction.getAmount())
                    .transactionType(transaction.getTransactionType().toString())
                    .transactionDate(transaction.getTransactionDate())
                    .build();

            if (transaction.getTargetAccountNumber() != null) {
                accountTransactionDetails.setTargetAccountNumber(transaction.getTargetAccountNumber().toString());
            } else {
                accountTransactionDetails.setTargetAccountNumber("N/A");
            }
            accountTransactionsDetails.add(accountTransactionDetails);
        });

        return accountTransactionsDetails;
    }

    private void saveAccountTransaction(AccountTransaction transaction) {
        accountTransactionRepository.save(transaction);
    }
}