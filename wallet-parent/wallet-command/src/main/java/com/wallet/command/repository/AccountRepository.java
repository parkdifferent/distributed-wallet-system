package com.wallet.command.repository;

import com.wallet.command.aggregate.AccountAggregate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 账户仓储接口
 */
@Repository
public interface AccountRepository extends CrudRepository<AccountAggregate, String> {
    // 基础的CRUD操作由CrudRepository提供
}
