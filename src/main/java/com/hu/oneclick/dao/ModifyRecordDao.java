package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.domain.ModifyRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModifyRecordDao extends BaseMapper<ModifyRecord> {

    int inserts(@Param("modifyRecord") List<ModifyRecord> modifyRecord);

    List<ModifyRecord> queryList(ModifyRecord modifyRecord);
}
