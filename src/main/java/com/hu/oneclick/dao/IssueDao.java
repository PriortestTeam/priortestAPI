package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.Issue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface IssueDao extends BaseMapper<Issue> {


    Issue queryById(@Param("id") String id,@Param("masterId") String masterId);

    int update(Issue issue);

    @Page
    List<Issue> queryList(Issue issue);

    Issue queryCycleAndTest(String testCaseId, String testCycleId);

    List<Issue> findAll();
}
