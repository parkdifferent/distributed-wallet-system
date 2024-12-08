package com.wallet.dto;

import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 账户数据传输对象
 */
@Data
public class AccountDTO {
    /**
     * 账户ID
     */
    private String accountId;
    
    /**
     * 账户所有者ID
     */
    private String ownerId;
    
    /**
     * 资产类型
     */
    private AssetType assetType;
    
    /**
     * 账户状态
     */
    private AccountStatus status;
    
    /**
     * 可用余额
     */
    private BigDecimal availableBalance;
    
    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;
    
    /**
     * 余额上限
     */
    private BigDecimal balanceLimit;
    
    /**
     * Account creation time
     */
    private Instant createTime;
    
    /**
     * Last update time
     */
    private Instant updateTime;
}
