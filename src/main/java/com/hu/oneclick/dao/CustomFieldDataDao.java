package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.CustomFieldData;

public interface CustomFieldDataDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomFieldData record);

    int insertSelective(CustomFieldData record);

    CustomFieldData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomFieldData record);

    int updateByPrimaryKey(CustomFieldData record);
}