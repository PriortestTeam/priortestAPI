package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.Issue;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IssueDao extends BaseMapper<Issue> {


    Issue queryById(@Param("id") Long id);

    int updateByPrimaryKeySelective(Issue issue);

    @Page
    List<Issue> queryList(Issue issue);

    Issue queryCycleAndTest(String testCaseId, String testCycleId);

    List<Issue> findAll();

    /**
     * 根据runCaseId查询缺陷
     */
    List<Map<String, Object>> queryDefectsByRunCaseId(String runCaseId);

    /**
     * 批量根据runCaseId列表查询缺陷
     */
    List<Map<String, Object>> queryDefectsByRunCaseIds(List<String> runCaseIds);

    /**
     * 查询缺陷详情（包含关联的测试用例信息）
     */
    List<Map<String, Object>> queryDefectDetailsWithTestCases(@Param("projectId") Long projectId,
                                                               @Param("majorVersion") String majorVersion,
                                                               @Param("includeVersions") List<String> includeVersions,
                                                               @Param("testCycleIds") List<Long> testCycleIds);

    /**
     * 查询缺陷与运行用例的详细对应关系（用于调试和验证）
     */
    List<Map<String, Object>> queryDefectRunCaseMapping(@Param("projectId") Long projectId,
                                                         @Param("majorVersion") String majorVersion,
                                                         @Param("includeVersions") List<String> includeVersions,
                                                         @Param("testCycleIds") List<Long> testCycleIds);
}