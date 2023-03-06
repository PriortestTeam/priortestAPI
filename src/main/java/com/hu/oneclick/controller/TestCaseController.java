package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.TestCaseSaveDto;
import com.hu.oneclick.model.domain.param.TestCaseParam;
import com.hu.oneclick.server.service.TestCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("testCase")
@Api(tags = "测试用例")
@Slf4j
public class TestCaseController extends BaseController {

    @Resource
    private TestCaseService testCaseService;

    //@GetMapping("queryById/{id}")
    //@ApiOperation("查询测试用例")
    //public Resp<TestCase> queryById(@PathVariable Long id) {
    //    return testCaseService.queryById(id);
    //}
    //
    //@PostMapping("queryList")
    //@ApiOperation("查询测试用例")
    //public Resp<List<TestCase>> queryList(@RequestBody TestCaseDto testCase) {
    //    return testCaseService.queryList(testCase);
    //}
    //
    //@PostMapping("update")
    //public Resp<String> update(@RequestBody TestCase testCase) {
    //    return testCaseService.update(testCase);
    //}

    //@DeleteMapping("delete/{id}")
    //public Resp<String> delete(@PathVariable String id) {
    //    return testCaseService.delete(id);
    //}


    ///**
    // * 根据选择的故事id查询testcase 的需要的值
    // */
    //@GetMapping("queryTestNeedByFeatureId")
    //public Resp<Feature> queryTestNeedByFeatureId(@RequestParam String featureId) {
    //    return testCaseService.queryTestNeedByFeatureId(featureId);
    //}
    //
    //@PostMapping("addTestCase")
    //@ApiOperation("添加测试用例")
    //public Resp<String> addTestCase(@RequestBody TestCycleDto testCycleDto) {
    //    return testCaseService.addTestCase(testCycleDto);
    //}
    //
    //@PostMapping("updateAction")
    //@ApiOperation("更新action")
    //public Resp<List<TestCase>> updateAction(@RequestBody List<String> testCaseId, @RequestParam String actionType
    //        , @RequestParam String testCycleId) {
    //    return testCaseService.updateAction(testCaseId, actionType, testCycleId);
    //}

    @ApiOperation("列表")
    @PostMapping("/list")
    public Resp<PageInfo<TestCase>> list(@RequestBody TestCaseParam param) {
        startPage();
        List<TestCase> testCaseList = testCaseService.list(param);
        return new Resp.Builder<PageInfo<TestCase>>().setData(PageInfo.of(testCaseList)).ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated TestCaseSaveDto dto) {
        try {
            TestCase testCase = testCaseService.save(dto);
            return new Resp.Builder<TestCase>().setData(testCase).ok();
        } catch (Exception e) {
            log.error("新增测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCase>().fail();
        }
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public Resp<?> update(@RequestBody @Validated TestCaseSaveDto dto) {
        try {
            TestCase testCase = testCaseService.update(dto);
            return new Resp.Builder<TestCase>().setData(testCase).ok();
        } catch (Exception e) {
            log.error("修改测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCase>().fail();
        }
    }

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public Resp<TestCase> info(@PathVariable Long id) {
        TestCase testCase = testCaseService.info(id);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            testCaseService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCase>().fail();
        }
        return new Resp.Builder<TestCase>().ok();
    }

}
