package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysUserReference;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserReferenceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserReference record);

    int insertSelective(SysUserReference record);

    SysUserReference selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserReference record);

    int updateByPrimaryKey(SysUserReference record);

    int getReferenceTime(String userId);

    int getReferencePersonNo(String userId);
}