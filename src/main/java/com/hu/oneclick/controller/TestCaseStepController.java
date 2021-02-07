package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.server.service.TestCaseStepService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("testCaseStep")
public class TestCaseStepController {

    private final TestCaseStepService stepService;

    public TestCaseStepController(TestCaseStepService stepService) {
        this.stepService = stepService;
    }

    @GetMapping("queryById")
    public Resp<TestCaseStep> queryById(@RequestParam String id,@RequestParam String testCaseId) {
        return stepService.queryById(id,testCaseId);
    }

    @Page
    @PostMapping("queryList")
    public Resp<List<TestCaseStep>> queryList(@RequestBody TestCaseStep testCaseStep) {
        return stepService.queryList(testCaseStep);
    }

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody TestCaseStep testCaseStep) {
        return stepService.insert(testCaseStep);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody TestCaseStep testCaseStep) {
        return stepService.update(testCaseStep);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return stepService.delete(id);
    }

}
