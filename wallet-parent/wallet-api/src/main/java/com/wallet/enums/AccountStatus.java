package com.wallet.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 账户状态枚举
 */
@Getter
public enum AccountStatus {
    /**
     * 正常状态
     */
    ACTIVE("ACTIVE", "正常状态"),
    
    /**
     * 冻结状态
     */
    FROZEN("FROZEN", "冻结状态"),
    
    /**
     * 已注销
     */
    CLOSED("CLOSED", "已注销");

    @EnumValue
    private final String code;
    
    private final String description;

    AccountStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
