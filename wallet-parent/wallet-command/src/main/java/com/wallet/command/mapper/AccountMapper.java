package com.wallet.command.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wallet.command.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
}
