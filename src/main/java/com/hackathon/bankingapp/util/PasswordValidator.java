package com.hackathon.bankingapp.util;

import com.hackathon.bankingapp.exceptions.PasswordValidationException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PasswordValidator {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHARACTER_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    public static void validate(String password) {
        if (password.length() < 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long");
        }
        if (password.length() >= 128) {
            throw new PasswordValidationException("Password must be less than 128 characters long");
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            throw new PasswordValidationException("Password must contain at least one uppercase letter");
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            throw new PasswordValidationException("Password must contain at least one digit");
        }
        if (!SPECIAL_CHARACTER_PATTERN.matcher(password).find()) {
            throw new PasswordValidationException("Password must contain at least one special character");
        }
        if (WHITESPACE_PATTERN.matcher(password).find()) {
            throw new PasswordValidationException("Password cannot contain whitespace");
        }
    }
}