package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.entity.IssueJoinTestCase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IssueJoinTestCaseDao extends BaseMapper<IssueJoinTestCase> {


    int bindCaseDelete(@Param("issueId") String issueId);

    List<Issue> queryBindCaseList(@Param("issueId") String issueId);

}
