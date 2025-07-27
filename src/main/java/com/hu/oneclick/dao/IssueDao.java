package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.Issue;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IssueDao extends BaseMapper<Issue> {


    Issue queryById(@Param("id") String id,@Param("masterId") String masterId);

    int updateByPrimaryKeySelective(Issue issue);

    @Page
    List<Issue> queryList(Issue issue);

    Issue queryCycleAndTest(String testCaseId, String testCycleId);

    List<Issue> findAll();
    
    /**
     * 根据RunCaseId查询关联的缺陷信息
     * @param runCaseId RunCase ID
     * @return 缺陷信息列表
     */
    List<Map<String, Object>> queryDefectsByRunCaseId(String runCaseId);
}
