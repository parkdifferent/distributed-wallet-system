package com.wallet.dto;

import com.wallet.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 交易数据传输对象
 */
@Data
public class TransactionDTO {
    /**
     * 交易ID
     */
    private String transactionId;
    
    /**
     * 交易幂等ID
     */
    private String dedupId;
    
    /**
     * 交易类型
     */
    private TransactionType type;
    
    /**
     * 源账户ID
     */
    private String sourceAccountId;
    
    /**
     * 目标账户ID
     */
    private String targetAccountId;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
    /**
     * 交易状态
     */
    private String status;
    
    /**
     * 交易备注
     */
    private String remarks;
    
    /**
     * Transaction creation time
     */
    private Instant createTime;
    
    /**
     * Transaction completion time
     */
    private Instant completeTime;
}
