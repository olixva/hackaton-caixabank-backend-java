package com.hackathon.bankingapp.exceptions;

public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String message) {
        super(message);
    }
}