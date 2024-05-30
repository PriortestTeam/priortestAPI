package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestCycleDao extends BaseMapper<TestCycle> {


    TestCycle queryById(@Param("id") String id,@Param("masterId")  String masterId);

    Long getIdByTitle(@Param("title") String title,@Param("projectId") Long projectId);

    @Page
    List<TestCycle> queryAll(TestCycle testCycle);

    int updateByPrimaryKeySelective(TestCycle testCycle);

    List<LeftJoinDto> queryTitles(@Param("projectId")String projectId, @Param("title") String title, @Param("masterId") String masterId);

    List<Map<String,String>> getTestCycleVersion(String projectId, String env, String version);

    List<Map<String,Object>> getAllTestCycle(String projectId, String version, String env, String testCycleVersion);

    List<String> getTestCycleByProjectIdAndEvn(String projectId, String env, String testCycle);

    default TestCycle getByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId) {
        return new LambdaQueryChainWrapper<>(this)
                .eq(TestCycle::getId, id)
                .eq(TestCycle::getProjectId, projectId)
                .last("LIMIT 1")
                .one();
    }

}
