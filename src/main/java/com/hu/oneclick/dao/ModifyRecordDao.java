package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.ModifyRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface ModifyRecordDao extends BaseMapper<ModifyRecord> {

    int inserts(@Param("modifyRecord") List<ModifyRecord> modifyRecord);

    List<ModifyRecord> queryList(ModifyRecord modifyRecord);
}
