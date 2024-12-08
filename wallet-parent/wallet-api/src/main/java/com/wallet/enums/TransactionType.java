package com.wallet.enums;

/**
 * 交易类型枚举
 */
public enum TransactionType {
    /**
     * 充值
     */
    DEPOSIT,
    
    /**
     * 提现
     */
    WITHDRAW,
    
    /**
     * 转账
     */
    TRANSFER,
    
    /**
     * 冻结
     */
    FREEZE,
    
    /**
     * 解冻
     */
    UNFREEZE
}
