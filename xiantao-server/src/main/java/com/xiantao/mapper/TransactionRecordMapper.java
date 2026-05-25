package com.xiantao.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiantao.entity.TransactionRecord;

@Mapper
public interface TransactionRecordMapper extends BaseMapper<TransactionRecord> {
}
