package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.IssueJoinTestCase;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface IssueJoinTestCaseDao extends BaseMapper<IssueJoinTestCase> {


    int bindCaseDelete(@Param("issueId") String issueId);

    List<Issue> queryBindCaseList(@Param("issueId") String issueId);

}
