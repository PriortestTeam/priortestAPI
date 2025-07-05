package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.TestCycleScheduleModel;

public interface TestCycleScheduleModelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TestCycleScheduleModel record);

    int insertSelective(TestCycleScheduleModel record);

    TestCycleScheduleModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TestCycleScheduleModel record);

    int updateByPrimaryKey(TestCycleScheduleModel record);
}
