package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysUserOrderRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserOrderRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserOrderRecord record);

    int insertSelective(SysUserOrderRecord record);

    SysUserOrderRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserOrderRecord record);

    int updateByPrimaryKey(SysUserOrderRecord record);
}