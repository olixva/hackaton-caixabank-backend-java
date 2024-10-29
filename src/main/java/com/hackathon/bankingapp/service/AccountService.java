package com.hackathon.bankingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hackathon.bankingapp.dto.account.AccountInfoResponse;
import com.hackathon.bankingapp.dto.accounttransaction.AccountTransactionDetails;
import com.hackathon.bankingapp.exceptions.InsufficientBalanceException;
import com.hackathon.bankingapp.exceptions.InternalServerErrorException;
import com.hackathon.bankingapp.exceptions.InvalidPinException;
import com.hackathon.bankingapp.exceptions.NotFoundException;
import com.hackathon.bankingapp.models.Account;
import com.hackathon.bankingapp.models.AccountAsset;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.repositories.AccountAssetRepository;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.util.MailUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final String INVALID_PIN = "Invalid PIN";

    private final AccountRepository accountRepository;
    private final AccountAssetRepository accountAssetRepository;
    private final UserService userService;
    private final AccountTransactionService accountTransactionService;
    private final MarketPriceService marketPriceService;
    private final MailUtils mailUtils;

    public Account createAccount(User user) {
        Account account = Account.builder()
                .balance(BigDecimal.valueOf(0.00))
                .accountNumber(UUID.randomUUID())
                .user(user)
                .build();

        return saveAccount(account);
    }

    public Account getAccount(UUID accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found for the given account number: " + accountNumber));
    }

    private Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public AccountInfoResponse getAccountInfo(Account account) {
        return AccountInfoResponse.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    public void createPin(User user, String password, String pin) {
        if (userService.checkPassword(user, password)) {
            user.getAccount().setPin(pin);
            userService.saveUser(user);
        } else {
            throw new BadCredentialsException("Access denied");
        }
    }

    public void updatePin(User user, String password, String oldPin, String newPin) {
        if (userService.checkPassword(user, password) && user.getAccount().getPin().equals(oldPin)) {
            user.getAccount().setPin(newPin);
            userService.saveUser(user);
        } else {
            throw new BadCredentialsException("Invalid password or old PIN");
        }
    }

    public void depositMoney(User user, String pin, BigDecimal amount) {
        checkPin(user, pin);

        user.getAccount().setBalance(user.getAccount().getBalance().add(amount));
        userService.saveUser(user);
        accountTransactionService.createDepositTransaction(user, amount);
    }

    public void withdrawMoney(User user, String pin, BigDecimal amount) {
        checkPin(user, pin);

        if (user.getAccount().getBalance().compareTo(amount) >= 0) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(amount));
            userService.saveUser(user);
            accountTransactionService.createWithdrawTransaction(user, amount);
        } else {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    public void manageSuscriptionPayment(User user, BigDecimal amount) {
        if (user.getAccount().getBalance().compareTo(amount) >= 0) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(amount));
            userService.saveUser(user);
            accountTransactionService.creaateSubscriptionTransaction(user, amount);
        } else {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    public void transferMoney(User user, String pin, BigDecimal amount, String targetAccountNumber) {
        checkPin(user, pin);
        Account targetAccount = getAccount(UUID.fromString(targetAccountNumber));
        if (user.getAccount().getBalance().compareTo(amount) >= 0) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(amount));
            targetAccount.setBalance(targetAccount.getBalance().add(amount));
            userService.saveUser(user);
            saveAccount(targetAccount);
            accountTransactionService.createTransferTransaction(user, amount, targetAccount.getAccountNumber());
        } else {
            throw new InsufficientBalanceException("Insufficient balance");
        }

    }

    public List<AccountTransactionDetails> getTransactionHistory(Account account) {
        return accountTransactionService.getAccountTransactions(account.getAccountNumber());
    }

    public void buyAsset(User user, String pin, String assetSymbol, BigDecimal cashAmount) {
        checkPin(user, pin);
        try {
            if (user.getAccount().getBalance().compareTo(cashAmount) >= 0) {
                user.getAccount().setBalance(user.getAccount().getBalance().subtract(cashAmount));
                userService.saveUser(user);

                // Calculate the amount of asset that can be purchased
                BigDecimal purchasePrice = marketPriceService.getAssetPrice(assetSymbol);
                BigDecimal assetAmount = cashAmount.divide(purchasePrice, 2, RoundingMode.HALF_UP);

                // Search for the asset in the account assets
                AccountAsset accountAsset = accountAssetRepository.findByAccountAndAssetSymbol(user.getAccount(), assetSymbol)
                        .orElse(null);

                if (accountAsset != null) {
                    accountAsset.setQuantity(accountAsset.getQuantity().add(assetAmount));
                    accountAsset.setPurchasePrice(purchasePrice);
                    accountAssetRepository.save(accountAsset);
                } else {
                    accountAssetRepository.save(
                            AccountAsset.builder()
                                    .account(user.getAccount())
                                    .assetSymbol(assetSymbol)
                                    .quantity(assetAmount)
                                    .purchasePrice(purchasePrice)
                                    .build()
                    );
                }

                sendAssetPurchaseMail(user, assetSymbol, assetAmount, cashAmount);
                accountTransactionService.createAssetPurchaseTransaction(user, cashAmount);

            } else {
                throw new InternalServerErrorException("Internal error occurred while purchasing the asset.");
            }

        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Internal error occurred while purchasing the asset.");
        }
    }

    public void buyAsset(User user, String pin, String assetSymbol, BigDecimal cashAmount, BigDecimal purchasePrice) {

        checkPin(user, pin);

        if (user.getAccount().getBalance().compareTo(cashAmount) >= 0) {
            user.getAccount().setBalance(user.getAccount().getBalance().subtract(cashAmount));
            userService.saveUser(user);

            // Calculate the amount of asset that can be purchased
            BigDecimal assetAmount = cashAmount.divide(purchasePrice, 2, RoundingMode.HALF_UP);

            // Search for the asset in the account assets
            AccountAsset accountAsset = accountAssetRepository.findByAccountAndAssetSymbol(user.getAccount(), assetSymbol)
                    .orElse(null);

            if (accountAsset != null) {
                accountAsset.setQuantity(accountAsset.getQuantity().add(assetAmount));
                accountAsset.setPurchasePrice(purchasePrice);
                accountAssetRepository.save(accountAsset);
            } else {
                accountAssetRepository.save(
                        AccountAsset.builder()
                                .account(user.getAccount())
                                .assetSymbol(assetSymbol)
                                .quantity(assetAmount)
                                .purchasePrice(purchasePrice)
                                .build()
                );
            }

            sendAssetPurchaseMail(user, assetSymbol, assetAmount, cashAmount);
            accountTransactionService.createAssetPurchaseTransaction(user, cashAmount);

        } else {
            throw new InternalServerErrorException("Internal error occurred while purchasing the asset.");
        }
    }

    private void sendAssetPurchaseMail(User user, String assetSymbol, BigDecimal assetAmount, BigDecimal cashAmount) {
        List<AccountAsset> accountAssets = accountAssetRepository.findByAccount(user.getAccount());
        BigDecimal currentHoldings = accountAssets.stream()
                .filter(accountAsset -> accountAsset.getAssetSymbol().equals(assetSymbol))
                .map(AccountAsset::getQuantity)
                .findFirst()
                .orElse(BigDecimal.ZERO);
        StringBuilder assetSummaryBuilder = new StringBuilder();
        BigDecimal netWorth = user.getAccount().getBalance();

        for (AccountAsset accountAsset : accountAssets) {
            assetSummaryBuilder
                    .append("- ").append(accountAsset.getAssetSymbol())
                    .append(": ")
                    .append(accountAsset.getQuantity())
                    .append(" units purchased at $")
                    // Purchase price of the asset with 2 decimal places
                    .append(accountAsset.getPurchasePrice().setScale(2, RoundingMode.HALF_UP))
                    .append("\n");
            netWorth = netWorth.add(accountAsset.getPurchasePrice());
        }

        mailUtils.sendAssetPurchaseMail(user, assetSymbol, assetAmount, cashAmount, currentHoldings, assetSummaryBuilder, netWorth);
    }

    public void sellAsset(User user, String pin, String assetSymbol, BigDecimal quantity) {
        checkPin(user, pin);
        try {
            AccountAsset accountAsset = accountAssetRepository.findByAccountAndAssetSymbol(user.getAccount(), assetSymbol)
                    .orElseThrow(() -> new InternalServerErrorException("Internal error occurred while selling the asset."));

            if (accountAsset.getQuantity().compareTo(quantity) >= 0) {
                BigDecimal sellPrice = marketPriceService.getAssetPrice(assetSymbol);
                BigDecimal cashAmount = sellPrice.multiply(quantity);

                user.getAccount().setBalance(user.getAccount().getBalance().add(cashAmount));
                userService.saveUser(user);

                accountAsset.setQuantity(accountAsset.getQuantity().subtract(quantity));
                accountAssetRepository.save(accountAsset);

                sendAssetSaleMail(user, assetSymbol, quantity, cashAmount);
                accountTransactionService.createAssetSaleTransaction(user, cashAmount);

            } else {
                throw new InternalServerErrorException("Internal error occurred while selling the asset.");
            }

        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Internal error occurred while selling the asset.");
        }
    }

    public void sellAsset(User user, String pin, String assetSymbol, BigDecimal quantity, BigDecimal sellPrice) {
        checkPin(user, pin);

        AccountAsset accountAsset = accountAssetRepository.findByAccountAndAssetSymbol(user.getAccount(), assetSymbol)
                .orElseThrow(() -> new InternalServerErrorException("Internal error occurred while selling the asset."));

        if (accountAsset.getQuantity().compareTo(quantity) >= 0) {
            BigDecimal cashAmount = sellPrice.multiply(quantity);

            user.getAccount().setBalance(user.getAccount().getBalance().add(cashAmount));
            userService.saveUser(user);

            accountAsset.setQuantity(accountAsset.getQuantity().subtract(quantity));
            accountAssetRepository.save(accountAsset);

            sendAssetSaleMail(user, assetSymbol, quantity, cashAmount);
            accountTransactionService.createAssetSaleTransaction(user, cashAmount);

        } else {
            throw new InternalServerErrorException("Internal error occurred while selling the asset.");
        }
    }

    private void sendAssetSaleMail(User user, String assetSymbol, BigDecimal assetAmount, BigDecimal cashAmount) {
        List<AccountAsset> accountAssets = accountAssetRepository.findByAccount(user.getAccount());
        BigDecimal remainingHoldings = accountAssets.stream()
                .filter(accountAsset -> accountAsset.getAssetSymbol().equals(assetSymbol))
                .map(AccountAsset::getQuantity)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal totalInvestment = accountAssets.stream()
                .filter(accountAsset -> accountAsset.getAssetSymbol().equals(assetSymbol))
                .map(accountAsset -> accountAsset.getPurchasePrice().multiply(accountAsset.getQuantity()))
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal totalGainLoss = cashAmount.subtract(totalInvestment);
        StringBuilder assetSummaryBuilder = new StringBuilder();
        for (AccountAsset accountAsset : accountAssets) {
            assetSummaryBuilder
                    .append("- ").append(accountAsset.getAssetSymbol())
                    .append(": ")
                    .append(accountAsset.getQuantity())
                    .append(" units purchased at $")
                    // Purchase price of the asset with 2 decimal places
                    .append(accountAsset.getPurchasePrice().setScale(2, RoundingMode.HALF_UP))
                    .append("\n");
        }

        mailUtils.sendAssetSaleMail(user, assetSymbol, assetAmount, totalGainLoss, remainingHoldings, assetSummaryBuilder, user.getAccount().getBalance(), getNetWorth(user.getAccount()));
    }

    public void manageAutoInvest(User user) {
        List<AccountAsset> userAssets = accountAssetRepository.findByAccount(user.getAccount());

        for (AccountAsset asset : userAssets) {
            try {
                // Get the current price of the asset
                BigDecimal currentPrice = marketPriceService.getAssetPrice(asset.getAssetSymbol());

                // Rules for auto investment
                BigDecimal purchasePrice = asset.getPurchasePrice();
                BigDecimal dropThreshold = purchasePrice.multiply(BigDecimal.valueOf(0.95)); // 5% drop
                BigDecimal riseThreshold = purchasePrice.multiply(BigDecimal.valueOf(1.05)); // 5% rise

                // Buy if the price drops by 5% or more
                if (currentPrice.compareTo(dropThreshold) <= 0) {
                    // Buy 10% of the balance
                    BigDecimal investmentAmount = user.getAccount().getBalance().multiply(BigDecimal.valueOf(0.1));
                    if (user.getAccount().getBalance().compareTo(investmentAmount) >= 0) {
                        buyAsset(user, user.getAccount().getPin(), asset.getAssetSymbol(), investmentAmount, currentPrice);
                    }
                }

                // Sell if the price rises by 5% or more
                if (currentPrice.compareTo(riseThreshold) >= 0) {
                    // Sell 10% of the asset
                    BigDecimal sellQuantity = asset.getQuantity().multiply(BigDecimal.valueOf(0.1));
                    if (sellQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        sellAsset(user, user.getAccount().getPin(), asset.getAssetSymbol(), sellQuantity, currentPrice);
                    }
                }

            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException("Error processing auto invest for asset: " + asset.getAssetSymbol());
            }
        }
    }

    public Map<String, BigDecimal> getAssets(Account account) {
        List<AccountAsset> accountAssets = accountAssetRepository.findByAccount(account);
        Map<String, BigDecimal> assetMap = new HashMap<>();

        for (AccountAsset accountAsset : accountAssets) {
            assetMap.put(accountAsset.getAssetSymbol(), accountAsset.getQuantity());
        }

        return assetMap;
    }

    public BigDecimal getNetWorth(Account account) {
        List<AccountAsset> accountAssets = accountAssetRepository.findByAccount(account);
        BigDecimal netWorth = account.getBalance();

        for (AccountAsset accountAsset : accountAssets) {
            try {
                netWorth = netWorth.add(accountAsset.getQuantity().multiply(marketPriceService.getAssetPrice(accountAsset.getAssetSymbol())));
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException("Internal error occurred while selling the asset.");
            }
        }

        return netWorth;
    }

    public void checkPin(User user, String pin) {
        if (user.getAccount().getPin() == null || !user.getAccount().getPin().equals(pin)) {
            throw new InvalidPinException(INVALID_PIN);
        }
    }
}
