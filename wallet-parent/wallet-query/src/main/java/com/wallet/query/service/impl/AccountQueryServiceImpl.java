package com.wallet.query.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import com.wallet.query.entity.AccountEntity;
import com.wallet.query.mapper.AccountMapper;
import com.wallet.query.service.AccountQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountQueryServiceImpl implements AccountQueryService {
    private final AccountMapper accountMapper;

    @Override
    public AccountEntity getAccountById(String accountId) {
        return accountMapper.selectById(accountId);
    }

    @Override
    public Optional<AccountEntity> findById(String accountId) {
        return Optional.ofNullable(accountMapper.selectById(accountId));
    }

    @Override
    public List<AccountEntity> findByStatus(AccountStatus status) {
        LambdaQueryWrapper<AccountEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountEntity::getStatus, status);
        return accountMapper.selectList(queryWrapper);
    }

    @Override
    public List<AccountEntity> findByOwnerIdAndAssetType(String ownerId, AssetType assetType) {
        LambdaQueryWrapper<AccountEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountEntity::getOwnerId, ownerId)
                .eq(AccountEntity::getAssetType, assetType);
        return accountMapper.selectList(queryWrapper);
    }

    @Override
    public List<AccountEntity> findByAssetType(AssetType assetType) {
        LambdaQueryWrapper<AccountEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountEntity::getAssetType, assetType);
        return accountMapper.selectList(queryWrapper);
    }

    @Override
    public List<AccountEntity> findByOwnerId(String ownerId) {
        LambdaQueryWrapper<AccountEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountEntity::getOwnerId, ownerId);
        return accountMapper.selectList(queryWrapper);
    }
}
