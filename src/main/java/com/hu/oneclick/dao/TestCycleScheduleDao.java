package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.TestCycleSchedule;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TestCycleScheduleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TestCycleSchedule record);

    int insertSelective(TestCycleSchedule record);

    TestCycleSchedule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TestCycleSchedule record);

    int updateByPrimaryKey(TestCycleSchedule record);

    List<TestCycleSchedule> selectAll();

    List<TestCycleSchedule> selectAllByRuntime(Date date);
}
