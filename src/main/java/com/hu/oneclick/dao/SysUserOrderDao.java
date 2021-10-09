package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysUserOrder;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserOrderDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserOrder record);

    int insertSelective(SysUserOrder record);

    SysUserOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserOrder record);

    int updateByPrimaryKey(SysUserOrder record);
}