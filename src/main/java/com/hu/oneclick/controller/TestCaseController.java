package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.TestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleDto;
import com.hu.oneclick.server.service.TestCaseService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("testCase")
public class TestCaseController {

    private final TestCaseService testCaseService;

    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }


    @GetMapping("queryById/{id}")
    public Resp<TestCase> queryById(@PathVariable String id) {
        return testCaseService.queryById(id);
    }


    @PostMapping("queryList")
    public Resp<List<TestCase>> queryList(@RequestBody TestCaseDto testCase) {
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


    /**
     * 根据选择的故事id查询testcase 的需要的值
     */
    @GetMapping("queryTestNeedByFeatureId")
    public Resp<Feature> queryTestNeedByFeatureId(@RequestParam String featureId) {
        return testCaseService.queryTestNeedByFeatureId(featureId);
    }

    @PostMapping("addTestCase")
    public Resp<String> addTestCase(@RequestBody TestCycleDto testCycleDto) {
        return testCaseService.addTestCase(testCycleDto);
    }
}
