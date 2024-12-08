package com.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 余额变更数据传输对象
 */
@Data
public class BalanceChangeDTO {
    /**
     * 账户ID
     */
    private String accountId;
    
    /**
     * 变更金额
     */
    private BigDecimal amount;
    
    /**
     * 变更前余额
     */
    private BigDecimal beforeBalance;
    
    /**
     * 变更后余额
     */
    private BigDecimal afterBalance;
    
    /**
     * 关联交易ID
     */
    private String transactionId;
    
    /**
     * 变更类型
     */
    private String changeType;
    
    /**
     * 变更说明
     */
    private String description;
}
