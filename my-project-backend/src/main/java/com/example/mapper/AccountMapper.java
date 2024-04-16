package com.example.mapper;

import com.example.entity.dto.DtoAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<DtoAccount> {

}
