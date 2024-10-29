package com.hackathon.bankingapp.service;

import com.hackathon.bankingapp.exceptions.NotFoundException;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.util.MailUtils;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;
    private final MailUtils mailSender;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public String sendOtp(String identifier) {
        User user = userService.getUserByIdentifier(identifier);

        String token = generateOtp(user);
        mailSender.sendPasswordResetEmail(user, token);

        return user.getEmail();
    }

    private String generateOtp(User user) {
        // Generate a random token and save it to the user
        String otp = String.format("%06d", random.nextInt(999999));
        user.setOtp(otp);
        userService.saveUser(user);

        return otp;
    }

    public String verifyOtp(String identifier, String otp) {
        User user = userService.getUserByIdentifier(identifier);

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new NotFoundException("Invalid OTP");
        }

        String passwordResetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(passwordResetToken);
        user.setOtp(null);
        userService.saveUser(user);

        return passwordResetToken;
    }

    public void resetPassword(String identifier, String resetToken, String newPassword) {
        User user = userService.getUserByIdentifier(identifier);

        if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(resetToken)) {
            throw new NotFoundException("Invalid reset token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        userService.saveUser(user);
    }
}