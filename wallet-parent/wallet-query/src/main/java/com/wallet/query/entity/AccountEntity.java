package com.wallet.query.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@TableName("t_account")
public class AccountEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private String accountId;
    
    private String ownerId;
    
    private AssetType assetType;
    
    private AccountStatus status;
    
    private BigDecimal balance;
    
    private BigDecimal minBalance;
    
    private BigDecimal maxBalance;
    
    private String currency;
    
    @TableField(fill = FieldFill.INSERT)
    private Instant createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    @Version
    private Integer version;
    
    @TableLogic
    private Integer deleted;
}
