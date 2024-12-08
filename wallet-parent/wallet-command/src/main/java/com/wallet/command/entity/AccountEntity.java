package com.wallet.command.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wallet.enums.AccountStatus;
import com.wallet.enums.AssetType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_account")
public class AccountEntity {
    
    @TableId(type = IdType.INPUT)
    private String id;
    
    private AssetType assetType;
    
    private AccountStatus status;
    
    private BigDecimal balance;
    
    private BigDecimal minBalance;
    
    private BigDecimal maxBalance;
    
    private String currency;
    
    private String ownerId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @Version
    private Integer version;
    
    @TableLogic
    private Integer deleted;
}
