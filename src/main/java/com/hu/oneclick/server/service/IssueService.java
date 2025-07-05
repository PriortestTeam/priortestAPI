package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.param.IssueParam;
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

    /**
     * 获取缺陷实体
     * @param projectId
     * @param issueId
     * @return
     */
    Issue retrieveIssueStatusAsPerIssueId(Long projectId, Long issueId);

    /**
     * 第一种参数类型：普通列表查询
     * @param param
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Issue> listWithViewFilter(IssueParam param, int pageNum, int pageSize);

    /**
     * 第二种参数类型：视图过滤查询
     * @param viewId
     * @param projectId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Issue> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize);

    /**
     * 第三种参数类型：字段过滤查询
     * @param fieldNameEn
     * @param value
     * @param scopeName
     * @param scopeId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Issue> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize);
}
