package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.Issue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IssueDao extends BaseMapper<Issue> {


    Issue queryById(@Param("id") String id,@Param("masterId") String masterId);

    int updateByPrimaryKeySelective(Issue issue);

    @Page
    List<Issue> queryList(Issue issue);

    Issue queryCycleAndTest(String testCaseId, String testCycleId);

    List<Issue> findAll();
}
