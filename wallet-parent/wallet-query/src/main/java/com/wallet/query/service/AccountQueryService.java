package com.wallet.query.service;

import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import com.wallet.query.entity.AccountEntity;
import java.util.List;
import java.util.Optional;

public interface AccountQueryService {
    Optional<AccountEntity> findById(String accountId);
    
    List<AccountEntity> findByOwnerId(String ownerId);
    
    List<AccountEntity> findByAssetType(AssetType assetType);
    
    List<AccountEntity> findByStatus(AccountStatus status);
    
    List<AccountEntity> findByOwnerIdAndAssetType(String ownerId, AssetType assetType);
    
    AccountEntity getAccountById(String accountId);
}
