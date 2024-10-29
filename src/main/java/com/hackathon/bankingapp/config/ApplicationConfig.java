package com.hackathon.bankingapp.config;

import com.hackathon.bankingapp.exceptions.NotFoundException;
import com.hackathon.bankingapp.repositories.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCryptPasswordEncoder to hash passwords
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            UUID accountNumber = null;
            try {
                accountNumber = UUID.fromString(identifier);
            } catch (IllegalArgumentException e) {
                return userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new NotFoundException("User not found for the given identifier: " + identifier));
            }

            return userRepository.findByAccountAccountNumber(accountNumber)
                    .orElseThrow(() -> new NotFoundException("User not found for the given identifier: " + identifier));
        };
    }
}
