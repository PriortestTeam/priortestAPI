package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysOrderDiscount;
import org.springframework.stereotype.Repository;

@Repository
public interface SysOrderDiscountDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysOrderDiscount record);

    int insertSelective(SysOrderDiscount record);

    SysOrderDiscount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysOrderDiscount record);

    int updateByPrimaryKey(SysOrderDiscount record);
}