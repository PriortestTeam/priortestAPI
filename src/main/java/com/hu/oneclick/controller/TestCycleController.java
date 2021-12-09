package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.TestCycleScheduleModel;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleDto;
import com.hu.oneclick.server.service.TestCycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("testCycle")
@Api(tags = "测试周期")
public class TestCycleController {

    private final TestCycleService testCycleService;

    public TestCycleController(TestCycleService testCycleService) {
        this.testCycleService = testCycleService;
    }


    @GetMapping("queryById/{id}")
    public Resp<TestCycle> queryById(@PathVariable String id) {
        return testCycleService.queryById(id);
    }


    @PostMapping("queryList")
    public Resp<List<TestCycle>> queryList(@RequestBody TestCycleDto testCycle) {
        return testCycleService.queryList(testCycle);
    }

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody TestCycle testCycle) {
        return testCycleService.insert(testCycle);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody TestCycle testCycle) {
        return testCycleService.update(testCycle);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return testCycleService.delete(id);
    }





    @Page
    @PostMapping("queryBindCaseList")
    public  Resp<List<TestCase>> queryBindCaseList(@RequestParam String testCycleId) {
        return testCycleService.queryBindCaseList(testCycleId);
    }

    @PostMapping("executeTestCase")
    public Resp<String> executeTestCase(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
        return testCycleService.executeTestCase(executeTestCaseDto);
    }

    @PostMapping("bindCaseInsert")
    public Resp<String> bindCaseInsert(@RequestBody TestCycleJoinTestCase testCycleJoinTestCase) {
        return testCycleService.bindCaseInsert(testCycleJoinTestCase);
    }

    @DeleteMapping("bindCaseDelete/{id}")
    public Resp<String> bindCaseDelete(@PathVariable String id) {
        return testCycleService.bindCaseDelete(id);
    }
    /* WJK新增 BEGIN*/
    @PostMapping("runTestCycleTc")
    public Resp<Map<String,Object>> runTestCycleTc(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
        return testCycleService.runTestCycleTc(executeTestCaseDto);
    }

    @PostMapping("excute")
    public Resp<Map<String,Object>> excute(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
        return testCycleService.excute(executeTestCaseDto);
    }

    @PostMapping("mergeIssue")
    public Resp<String> mergeIssue(@RequestBody Issue issue) {
        return testCycleService.mergeIssue(issue);
    }

    @PostMapping("queryIssueByIdOrName")
    public Resp<Map<String, Object>> queryIssueByIdOrName(@RequestBody Issue issue) {
        return testCycleService.queryIssueByIdOrName(issue);
    }
    /* WJK新增 END*/

    @PostMapping("addSchedule")
    @ApiModelProperty("添加计划")
    public Resp<String> addSchedule(@RequestBody TestCycleScheduleModel model) {
        return testCycleService.addSchedule(model);
    }


}
