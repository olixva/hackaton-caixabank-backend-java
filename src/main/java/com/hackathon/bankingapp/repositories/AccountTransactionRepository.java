package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.models.AccountTransaction;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    List<AccountTransaction> findByAccountAccountNumber(UUID accountNumber);
}