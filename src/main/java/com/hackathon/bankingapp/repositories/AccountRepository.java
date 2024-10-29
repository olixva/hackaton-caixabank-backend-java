package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.models.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(UUID accountNumber);
}