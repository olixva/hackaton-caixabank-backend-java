package com.hackathon.bankingapp.controllers;

import com.hackathon.bankingapp.dto.account.AccountInfoResponse;
import com.hackathon.bankingapp.dto.account.CreatePinRequest;
import com.hackathon.bankingapp.dto.account.AccountMsgResponse;
import com.hackathon.bankingapp.dto.accounttransaction.AccountTransactionDetails;
import com.hackathon.bankingapp.dto.accounttransaction.DepositWithdrawMoneyRequest;
import com.hackathon.bankingapp.dto.accounttransaction.TransferMoneyRequest;
import com.hackathon.bankingapp.dto.account.UpdatePinRequest;
import com.hackathon.bankingapp.dto.assets.BuyAssetsRequest;
import com.hackathon.bankingapp.dto.assets.SellAssetsRequest;
import com.hackathon.bankingapp.models.User;
import com.hackathon.bankingapp.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Operations related to accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Get account details",
            description = "Returns the account details for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "dashboard/account")
    public ResponseEntity<AccountInfoResponse> getAccountInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountInfo(user.getAccount()));
    }

    @Operation(
            summary = "Create a PIN",
            description = "Creates a PIN for the account of an authenticated user, needs to send the password and the new PIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/pin/create")
    public ResponseEntity<AccountMsgResponse> getAccountInfo(@AuthenticationPrincipal User user, @Valid @RequestBody CreatePinRequest createPinRequest) {
        accountService.createPin(user, createPinRequest.getPassword(), createPinRequest.getPin());
        return ResponseEntity.ok(
                AccountMsgResponse.builder()
                        .msg("PIN created successfully")
                        .build()
        );
    }

    @Operation(
            summary = "Update PIN",
            description = "Update the PIN for the account of an authenticated user, needs to send the password and the new PIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/pin/update")
    public ResponseEntity<AccountMsgResponse> updatePin(@AuthenticationPrincipal User user, @Valid @RequestBody UpdatePinRequest updatePinRequest) {
        accountService.updatePin(user, updatePinRequest.getPassword(), updatePinRequest.getOldPin(), updatePinRequest.getNewPin());
        return ResponseEntity.ok(
                AccountMsgResponse.builder()
                        .msg("PIN updated successfully")
                        .build()
        );
    }

    @Operation(
            summary = "Deposit money",
            description = "Deposit money to the account of an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/deposit")
    public ResponseEntity<AccountMsgResponse> depositMoney(@AuthenticationPrincipal User user, @Valid @RequestBody DepositWithdrawMoneyRequest depositRequest) {
        accountService.depositMoney(user, depositRequest.getPin(), depositRequest.getAmount());
        return ResponseEntity.ok(
                AccountMsgResponse.builder()
                        .msg("Cash deposited successfully")
                        .build()
        );
    }

    @Operation(
            summary = "Withdraw money",
            description = "Withdraw money from the account of an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/withdraw")
    public ResponseEntity<AccountMsgResponse> withdrawMoney(@AuthenticationPrincipal User user, @Valid @RequestBody DepositWithdrawMoneyRequest withdrawRequest) {
        accountService.withdrawMoney(user, withdrawRequest.getPin(), withdrawRequest.getAmount());
        return ResponseEntity.ok(
                AccountMsgResponse.builder()
                        .msg("Cash withdrawn successfully")
                        .build()
        );
    }

    @Operation(
            summary = "Transfer money",
            description = "Transfer money from the account of an authenticated user to another account",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/fund-transfer")
    public ResponseEntity<AccountMsgResponse> transferMoney(@AuthenticationPrincipal User user, @Valid @RequestBody TransferMoneyRequest transferMoneyRequest) {
        accountService.transferMoney(user, transferMoneyRequest.getPin(), transferMoneyRequest.getAmount(), transferMoneyRequest.getTargetAccountNumber());
        return ResponseEntity.ok(
                AccountMsgResponse.builder()
                        .msg("Fund transferred successfully")
                        .build()
        );
    }

    @Operation(
            summary = "Get transaction history",
            description = "Returns the transaction history for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "account/transactions")
    public ResponseEntity<List<AccountTransactionDetails>> getTransactionHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getTransactionHistory(user.getAccount()));
    }

    @Operation(
            summary = "Show assets",
            description = "Show assets for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "account/assets")
    public ResponseEntity<Map<String, BigDecimal>> getAssets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAssets(user.getAccount()));
    }

    @Operation(
            summary = "Show net worth",
            description = "Show net worth for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(value = "account/net-worth")
    public ResponseEntity<BigDecimal> getNetWorth(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getNetWorth(user.getAccount()));
    }

    @Operation(
            summary = "Buy asset",
            description = "Buy asset for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/buy-asset")
    public ResponseEntity<String> buyAsset(@AuthenticationPrincipal User user, @Valid @RequestBody BuyAssetsRequest buyAssetRequest) {
        accountService.buyAsset(user, buyAssetRequest.getPin(), buyAssetRequest.getAssetSymbol(), buyAssetRequest.getAmount());
        return ResponseEntity.ok("Asset purchase successful.");
    }

    @Operation(
            summary = "Sell asset",
            description = "Sell asset for an authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "account/sell-asset")
    public ResponseEntity<String> sellAsset(@AuthenticationPrincipal User user, @Valid @RequestBody SellAssetsRequest sellAssetRequest) {
        accountService.sellAsset(user, sellAssetRequest.getPin(), sellAssetRequest.getAssetSymbol(), sellAssetRequest.getQuantity());
        return ResponseEntity.ok("Asset sale successful.");
    }
}