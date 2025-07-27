package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.TestCaseDataDto;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

/**
 * @author qingyang
 */
public interface TestCaseDao extends BaseMapper<TestCase> {


  TestCase queryById(@Param("id") String id, @Param("masterId") String masterId);

  int updateByPrimaryKeySelective(TestCase testCase);


  List<LeftJoinDto> queryTitles(@Param("projectId") String projectId, @Param("title") String title,
      @Param("masterId") String masterId);

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

  List<Map<String,Object>> queryTestCasesWithCasesByConditions(Map<String,Object> conditions);

    /**
     * 根据项目ID和版本列表查询计划测试用例总数
     */
    Integer countPlannedTestCasesByVersions(@Param("projectId") Long projectId, @Param("versions") List<String> versions);

    /**
     * 统计实际执行测试用例数（去重）
     */
    Integer countExecutedTestCasesByVersionsAndCycles(@Param("projectId") Long projectId,
                                                    @Param("majorVersion") List<String> majorVersion,
                                                    @Param("includeVersions") List<String> includeVersions,
                                                    @Param("testCycleIds") List<Long> testCycleIds);

    /**
     * 查询功能执行率详细信息
     */
    List<Map<String, Object>> queryExecutionDetails(@Param("projectId") Long projectId,
                                                     @Param("versions") List<String> versions,
                                                     @Param("testCycleIds") List<Long> testCycleIds);
}