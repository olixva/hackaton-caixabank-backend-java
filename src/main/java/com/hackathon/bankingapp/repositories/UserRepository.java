package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.models.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAccountAccountNumber(UUID accountNumber);
    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);
}