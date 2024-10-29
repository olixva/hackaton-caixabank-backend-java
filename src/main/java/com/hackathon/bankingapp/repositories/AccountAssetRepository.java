package com.hackathon.bankingapp.repositories;

import com.hackathon.bankingapp.models.Account;
import com.hackathon.bankingapp.models.AccountAsset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountAssetRepository extends JpaRepository<AccountAsset, Long> {
    List<AccountAsset> findByAccount(Account account);
    Optional<AccountAsset> findByAccountAndAssetSymbol(Account account, String assetSymbol);
}