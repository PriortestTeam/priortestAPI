package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.CustomFieldData;

import java.util.List;

public interface CustomFieldDataDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomFieldData record);

    int insertSelective(CustomFieldData record);

    CustomFieldData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomFieldData record);

    int updateByPrimaryKey(CustomFieldData record);

    List<CustomFieldData> getAllByScopeIdAndScope(String scope, String scopeId);

    List<CustomFieldData> findAllByUserIdAndScope(String projectId, String userId, String scope, String fieldName);
}