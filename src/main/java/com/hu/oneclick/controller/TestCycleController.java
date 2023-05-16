package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;
import com.hu.oneclick.model.domain.param.TestCycleParam;
import com.hu.oneclick.server.service.TestCycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("testCycle")
@Api(tags = "测试周期")
@Slf4j
public class TestCycleController extends BaseController {

    @Resource
    private TestCycleService testCycleService;


//    @GetMapping("queryById/{id}")
//    @ApiOperation("查询测试周期")
//    public Resp<TestCycle> queryById(@PathVariable String id) {
//        return testCycleService.queryById(id);
//    }
//
//
//    @PostMapping("queryList")
//    public Resp<List<TestCycle>> queryList(@RequestBody TestCycleDto testCycle) {
//        return testCycleService.queryList(testCycle);
//    }
//
//    @PostMapping("insert")
//    @ApiOperation("测试周期")
//    public Resp<String> insert(@RequestBody TestCycle testCycle) {
//        return testCycleService.insert(testCycle);
//    }
//
//    @PostMapping("update")
//    public Resp<String> update(@RequestBody TestCycle testCycle) {
//        return testCycleService.update(testCycle);
//    }
//
//    @DeleteMapping("delete/{id}")
//    public Resp<String> delete(@PathVariable String id) {
//        return testCycleService.delete(id);
//    }
//
//
//
//
//
//    @Page
//    @PostMapping("queryBindCaseList")
//    public  Resp<List<TestCase>> queryBindCaseList(@RequestParam String testCycleId) {
//        return testCycleService.queryBindCaseList(testCycleId);
//    }
//
//    @PostMapping("executeTestCase")
//    public Resp<String> executeTestCase(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
//        return testCycleService.executeTestCase(executeTestCaseDto);
//    }
//
//    @PostMapping("bindCaseInsert")
//    public Resp<String> bindCaseInsert(@RequestBody TestCycleJoinTestCase testCycleJoinTestCase) {
//        return testCycleService.bindCaseInsert(testCycleJoinTestCase);
//    }
//
//    @DeleteMapping("bindCaseDelete/{id}")
//    public Resp<String> bindCaseDelete(@PathVariable String id) {
//        return testCycleService.bindCaseDelete(id);
//    }
//    /* WJK新增 BEGIN*/
//    @PostMapping("runTestCycleTc")
//    public Resp<Map<String,Object>> runTestCycleTc(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
//        return testCycleService.runTestCycleTc(executeTestCaseDto);
//    }
//
//    @PostMapping("excute")
//    public Resp<Map<String,Object>> excute(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
//        return testCycleService.excute(executeTestCaseDto);
//    }
//
//    @PostMapping("mergeIssue")
//    public Resp<String> mergeIssue(@RequestBody Issue issue) {
//        return testCycleService.mergeIssue(issue);
//    }
//
//    @PostMapping("queryIssueByIdOrName")
//    public Resp<Map<String, Object>> queryIssueByIdOrName(@RequestBody Issue issue) {
//        return testCycleService.queryIssueByIdOrName(issue);
//    }
//    /* WJK新增 END*/
//
//    @PostMapping("addSchedule")
//    @ApiModelProperty("添加计划")
//    public Resp<String> addSchedule(@RequestBody TestCycleScheduleModel model) {
//        return testCycleService.addSchedule(model);
//    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public Resp<PageInfo<TestCycle>> list(@RequestBody @Validated TestCycleParam param) {
        startPage();
        List<TestCycle> testCycleList = testCycleService.list(param);
        return new Resp.Builder<PageInfo<TestCycle>>().setData(PageInfo.of(testCycleList)).ok();
    }

    @ApiOperation("新增")
    @PostMapping("/saveTestCycle")
    public Resp<?> save(@RequestBody @Validated TestCycleSaveDto dto) {
        try {
            TestCycle testCycle = testCycleService.save(dto);
            return new Resp.Builder<TestCycle>().setData(testCycle).ok();
        } catch (Exception e) {
            log.error("新增测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCycle>().fail();
        }
    }

    @ApiOperation("修改")
    @PutMapping("/updateTestCycle")
    public Resp<?> update(@RequestBody @Validated TestCycleSaveDto dto) {
        try {
            TestCycle testCycle = testCycleService.update(dto);
            return new Resp.Builder<TestCycle>().setData(testCycle).ok();
        } catch (Exception e) {
            log.error("修改测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCycle>().fail();
        }
    }

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public Resp<TestCycle> info(@PathVariable Long id) {
        TestCycle testCycle = testCycleService.info(id);
        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/deleteTestCycle/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            testCycleService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCycle>().fail();
        }
        return new Resp.Builder<TestCycle>().ok();
    }

    @ApiOperation("克隆")
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            testCycleService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

}
