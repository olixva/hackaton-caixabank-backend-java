package com.hackathon.bankingapp.service;

import com.hackathon.bankingapp.exceptions.InternalServerErrorException;
import com.hackathon.bankingapp.models.User;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final AccountService accountService;

    @Async
    public void subscribe(User user, String pin, BigDecimal amount, long intervalSeconds) {
        accountService.checkPin(user, pin);

        while (user.getAccount().getBalance().compareTo(amount) >= 0) {
            try {
                Thread.sleep(intervalSeconds * 1000);
                accountService.manageSuscriptionPayment(user, amount);
            } catch (InterruptedException e) {
                throw new InternalServerErrorException("Error while processing subscription");
            }
        }
    }

    @Async
    public void enableAutoInvest(User user, String pin) {
        accountService.checkPin(user, pin);

        while (true) {
            try {
                Thread.sleep(5000);
                accountService.manageAutoInvest(user);
            } catch (InterruptedException e) {
                throw new InternalServerErrorException("Error while processing auto invest");
            }
        }
    }
}