package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.IssueJoinTestCase;
import com.hu.oneclick.model.domain.dto.IssueDto;
import com.hu.oneclick.server.service.IssueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("issue")
@Api(tags = "缺陷")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("queryById/{id}")
    @ApiOperation("查询缺陷")
    public Resp<Issue> queryById(@PathVariable String id) {
        return issueService.queryById(id);
    }


    @PostMapping("queryList")
    public Resp<List<Issue>> queryList(@RequestBody IssueDto issue) {
        return issueService.queryList(issue);
    }

    @PostMapping("insert")
    @ApiOperation("插入缺陷")
    public Resp<String> insert(@RequestBody Issue issue) {
        return issueService.insert(issue);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody Issue issue) {
        return issueService.update(issue);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return issueService.delete(id);
    }



    @Page
    @PostMapping("queryBindCaseList")
    public  Resp<List<Issue>> queryBindCaseList(@RequestParam String issueId) {
        return issueService.queryBindCaseList(issueId);
    }

    @PostMapping("bindCaseInsert")
    public Resp<String> bindCaseInsert(@RequestBody IssueJoinTestCase issueJoinTestCase) {
        return issueService.bindCaseInsert(issueJoinTestCase);
    }

    @DeleteMapping("bindCaseDelete/{id}")
    public Resp<String> bindCaseDelete(@PathVariable String id) {
        return issueService.bindCaseDelete(id);
    }


}
