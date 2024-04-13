package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.param.IssueParam;
import com.hu.oneclick.model.domain.dto.IssueStatusDto;

import java.util.List;

public interface IssueService extends IService<Issue> {

//    Resp<Issue> queryById(String id);
//
//    Resp<List<Issue>> queryList(IssueDto issue);
//
//    Resp<String> insert(Issue issue);
//
//    Resp<String> update(Issue issue);
//
//    Resp<String> delete(String id);
//
//
//
//    Resp<List<Issue>> queryBindCaseList(String issueId);
//
//    Resp<String> bindCaseInsert(IssueJoinTestCase issueJoinTestCase);
//
//    Resp<String> bindCaseDelete(String testCaseId);

    /**
     * 列表
     *
     * @param param
     * @return
     */
    List<Issue> list(IssueParam param);

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    Issue add(IssueSaveDto dto);

    /**
     * 修改
     *
     * @param dto
     * @return
     */
    Issue edit(IssueSaveDto dto);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    Issue info(Long id);

    int studusedit(Issue issue,IssueStatusDto issueStatusDto);


    void clone(List<Long> ids);
}
