package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.TestCaseDataDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseDao extends BaseMapper<TestCase> {


    TestCase queryById(@Param("id") String id, @Param("masterId") String masterId);

    int updateByPrimaryKeySelective(TestCase testCase);


    List<LeftJoinDto> queryTitles(@Param("projectId") String projectId, @Param("title") String title, @Param("masterId") String masterId);

    @Page
    List<TestCase> queryList(TestCase testCase);


    List<String> getProjectVersion(String projectId);

    default TestCase getByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId) {
        return new LambdaQueryChainWrapper<>(this)
                .eq(TestCase::getId, id)
                .eq(TestCase::getProjectId, projectId)
                .last("LIMIT 1")
                .one();
    }

    List<TestCaseDataDto> getSelectAll(Long testCycleId);



}
