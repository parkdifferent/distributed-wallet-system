package com.wallet.query.controller;

import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import com.wallet.query.entity.AccountEntity;
import com.wallet.query.service.AccountQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountQueryController {
    private final AccountQueryService accountQueryService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountEntity> getAccount(@PathVariable String accountId) {
        return accountQueryService.findById(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<AccountEntity>> getAccountsByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(accountQueryService.findByOwnerId(ownerId));
    }

    @GetMapping("/asset-type/{assetType}")
    public ResponseEntity<List<AccountEntity>> getAccountsByAssetType(@PathVariable AssetType assetType) {
        return ResponseEntity.ok(accountQueryService.findByAssetType(assetType));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountEntity>> getAccountsByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(accountQueryService.findByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountEntity>> searchAccounts(
            @RequestParam String ownerId,
            @RequestParam AssetType assetType) {
        return ResponseEntity.ok(accountQueryService.findByOwnerIdAndAssetType(ownerId, assetType));
    }
}
