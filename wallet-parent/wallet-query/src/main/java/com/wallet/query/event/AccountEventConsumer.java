package com.wallet.query.event;

import com.wallet.event.*;
import com.wallet.query.entity.AccountEntity;
import com.wallet.query.mapper.AccountMapper;
import com.wallet.query.service.AccountQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.wallet.enums.AccountStatus;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventConsumer {

    private final AccountMapper accountMapper;
    private final AccountQueryService accountQueryService;

    @KafkaListener(topics = "wallet-events", groupId = "wallet-query-service")
    @Transactional
    public void consumeEvent(BaseEvent event) {
        log.info("Received event: {}", event);
        
        if (event instanceof AccountCreatedEvent) {
            handleAccountCreated((AccountCreatedEvent) event);
        } else if (event instanceof BalanceChangedEvent) {
            handleBalanceChanged((BalanceChangedEvent) event);
        } else if (event instanceof AccountFrozenEvent) {
            handleAccountFrozen((AccountFrozenEvent) event);
        } else if (event instanceof AccountUnfrozenEvent) {
            handleAccountUnfrozen((AccountUnfrozenEvent) event);
        } else if (event instanceof AccountClosedEvent) {
            handleAccountClosed((AccountClosedEvent) event);
        }
    }

    private void handleAccountCreated(AccountCreatedEvent event) {
        AccountEntity account = new AccountEntity();
        account.setAccountId(event.getAccountId());
        account.setOwnerId(event.getOperatorId());
        account.setBalance(event.getInitialBalance());
        account.setMinBalance(event.getMinBalance());
        account.setMaxBalance(event.getMaxBalance());
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(event.getTimestamp());
        account.setUpdatedAt(event.getTimestamp());
        account.setCreatedBy(event.getOperatorId());
        account.setUpdatedBy(event.getOperatorId());

        accountMapper.insert(account);
        log.info("Created account: {}", account);
    }

    private void handleBalanceChanged(BalanceChangedEvent event) {
        AccountEntity account = accountQueryService.getAccountById(event.getAccountId());
        if (account == null) {
            log.error("Account not found: {}", event.getAccountId());
            return;
        }

        account.setBalance(account.getBalance().add(event.getAmount()));
        account.setUpdatedAt(event.getTimestamp());
        account.setUpdatedBy(event.getOperatorId());

        accountMapper.updateById(account);
        log.info("Updated balance for account {}: {}", account.getAccountId(), account.getBalance());
    }

    private void handleAccountFrozen(AccountFrozenEvent event) {
        AccountEntity account = accountQueryService.getAccountById(event.getAccountId());
        if (account == null) {
            log.error("Account not found: {}", event.getAccountId());
            return;
        }

        account.setStatus(AccountStatus.FROZEN);
        account.setUpdatedAt(event.getTimestamp());
        account.setUpdatedBy(event.getOperatorId());

        accountMapper.updateById(account);
        log.info("Frozen account {}", account.getAccountId());
    }

    private void handleAccountUnfrozen(AccountUnfrozenEvent event) {
        AccountEntity account = accountQueryService.getAccountById(event.getAccountId());
        if (account == null) {
            log.error("Account not found: {}", event.getAccountId());
            return;
        }

        account.setStatus(AccountStatus.ACTIVE);
        account.setUpdatedAt(event.getTimestamp());
        account.setUpdatedBy(event.getOperatorId());

        accountMapper.updateById(account);
        log.info("Unfrozen account {}", account.getAccountId());
    }

    private void handleAccountClosed(AccountClosedEvent event) {
        AccountEntity account = accountQueryService.getAccountById(event.getAccountId());
        if (account == null) {
            log.error("Account not found: {}", event.getAccountId());
            return;
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setUpdatedAt(event.getTimestamp());
        account.setUpdatedBy(event.getOperatorId());

        accountMapper.updateById(account);
        log.info("Closed account {}", account.getAccountId());
    }
}
