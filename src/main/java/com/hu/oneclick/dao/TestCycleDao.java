package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCycle;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

public interface TestCycleDao extends BaseMapper<TestCycle> {


    TestCycle queryById(@Param("id") String id,@Param("masterId")  String masterId);

    List<TestCycle> queryAll(TestCycle testCycle);

    int update(TestCycle testCycle);

    List<Map<String, String>> queryTitles(@Param("projectId")String projectId, @Param("title") String title, @Param("masterId") String masterId);
}
