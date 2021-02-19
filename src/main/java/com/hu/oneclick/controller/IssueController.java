package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.IssueJoinTestCase;
import com.hu.oneclick.server.service.IssueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("issue")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("queryById/{id}")
    public Resp<Issue> queryById(@PathVariable String id) {
        return issueService.queryById(id);
    }

    @Page
    @PostMapping("queryList")
    public Resp<List<Issue>> queryList(@RequestBody Issue issue) {
        return issueService.queryList(issue);
    }

    @PostMapping("insert")
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
