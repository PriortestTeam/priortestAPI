package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.IssueJoinTestCase;
import com.hu.oneclick.model.domain.dto.IssueDto;

import java.util.List;

public interface IssueService {

    Resp<Issue> queryById(String id);

    Resp<List<Issue>> queryList(IssueDto issue);

    Resp<String> insert(Issue issue);

    Resp<String> update(Issue issue);

    Resp<String> delete(String id);



    Resp<List<Issue>> queryBindCaseList(String issueId);

    Resp<String> bindCaseInsert(IssueJoinTestCase issueJoinTestCase);

    Resp<String> bindCaseDelete(String testCaseId);

}
