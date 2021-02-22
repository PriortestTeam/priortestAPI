package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.server.service.SprintService;
import com.hu.oneclick.server.service.TestCaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("testCase")
public class TestCaseController {

    private final TestCaseService testCaseService;

    public TestCaseController( TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }


    @GetMapping("queryById/{id}")
    public Resp<TestCase> queryById(@PathVariable String id) {
        return testCaseService.queryById(id);
    }


    @PostMapping("queryList")
    public Resp<List<TestCase>> queryList(@RequestBody TestCase testCase) {
        return testCaseService.queryList(testCase);
    }

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody TestCase testCase) {
        return testCaseService.insert(testCase);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody TestCase testCase) {
        return testCaseService.update(testCase);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return testCaseService.delete(id);
    }

}
