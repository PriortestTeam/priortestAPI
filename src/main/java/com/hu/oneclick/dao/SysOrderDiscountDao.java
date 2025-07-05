package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysOrderDiscount;
import com.hu.oneclick.model.domain.dto.SysOrderDiscountDto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SysOrderDiscountDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysOrderDiscount record);

    int insertSelective(SysOrderDiscount record);

    SysOrderDiscount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysOrderDiscount record);

    int updateByPrimaryKey(SysOrderDiscount record);

    BigDecimal getNormalDiscount(SysOrderDiscountDto sysOrderDiscountDto);
}
