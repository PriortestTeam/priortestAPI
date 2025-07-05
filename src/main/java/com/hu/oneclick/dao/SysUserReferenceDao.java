package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysUserReference;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SysUserReferenceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserReference record);

    int insertSelective(SysUserReference record);

    SysUserReference selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserReference record);

    int updateByPrimaryKey(SysUserReference record);

    int getReferenceTime(String userId, Date startTime, Date endTime);

    int getReferencePersonNo(String userId, Date startTime, Date endTime);
}
