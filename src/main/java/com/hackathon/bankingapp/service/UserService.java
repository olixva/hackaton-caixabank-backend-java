package com.hackathon.bankingapp.service;

import com.hackathon.bankingapp.dto.user.RegisterRequest;
import com.hackathon.bankingapp.dto.user.UserInfoResponse;
import com.hackathon.bankingapp.exceptions.DuplicateEntryException;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.repositories.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByIdentifier(String identifier) {
        UUID accountNumber = null;
        try {
            accountNumber = UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        }

        return userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
    }

    public User createUser(RegisterRequest registerRequest) {
        checkDuplicateEntry(registerRequest);

        User user = User.builder()
                .name(registerRequest.getName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .address(registerRequest.getAddress())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();

        return saveUser(user);

    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserInfoResponse getUserInfo(User user) {
        return UserInfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .hashedPassword(user.getPassword())
                .accountNumber(user.getAccount().getAccountNumber())
                .build();
    }

    public void checkDuplicateEntry(RegisterRequest registerRequest) {

        userRepository.findByEmailOrPhoneNumber(registerRequest.getEmail(), registerRequest.getPhoneNumber())
                .ifPresent(user -> {
                            if (user.getEmail().equals(registerRequest.getEmail())) {
                                throw new DuplicateEntryException("Email already exists");
                            } else {
                                throw new DuplicateEntryException("Phone number already exists");
                            }
                        }
                );
    }

    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}
