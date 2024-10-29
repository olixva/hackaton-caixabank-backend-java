package com.hackathon.bankingapp.util;

import com.hackathon.bankingapp.exceptions.EmailValidationException;
import org.springframework.stereotype.Service;

@Service
public class EmailValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static void validate(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new EmailValidationException("Invalid email:" + email);
        }
    }
}