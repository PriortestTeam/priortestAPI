package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SystemConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SystemConfig record);

    int insertSelective(SystemConfig record);

    SystemConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SystemConfig record);

    int updateByPrimaryKey(SystemConfig record);

    int updateByKey(SystemConfig record);

    String getDate(String key);

    int deleteByKey(String key);
}