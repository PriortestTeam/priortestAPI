package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SystemConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    String getDateForKeyAndGroup(String key,String group);

    int deleteByKey(String key);

    List<String> getKeyForGroup(String group);

    String getDataUI(String key);
}