package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysUserOrder;

import java.util.Date;
import java.util.List;


public interface SysUserOrderDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserOrder record);

    int insertSelective(SysUserOrder record);

    SysUserOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserOrder record);

    int updateByUuidSelective(SysUserOrder sysUserOrder);

    int updateByPrimaryKey(SysUserOrder record);

    SysUserOrder getOrderOfUserId(String userId);

    List<SysUserOrder> listOrder(String userId);
}