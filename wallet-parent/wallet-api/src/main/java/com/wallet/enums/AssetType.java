package com.wallet.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 资产类型枚举
 */
@Getter
public enum AssetType {
    /**
     * 现金
     */
    CASH("CASH", "现金"),
    
    /**
     * 加密货币
     */
    CRYPTO("CRYPTO", "加密货币"),
    
    /**
     * 优惠券
     */
    COUPON("COUPON", "优惠券");

    @EnumValue
    private final String code;
    
    private final String description;

    AssetType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
