package com.wallet.query.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wallet.query.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
}
