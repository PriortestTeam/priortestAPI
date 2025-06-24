package com.hu.oneclick.controller;

import cn.hutool.core.util.ArrayUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.entity.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseBisDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;
import com.hu.oneclick.model.param.TestCycleParam;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import com.hu.oneclick.server.service.TestCycleService;
import com.hu.oneclick.server.service.TestCycleTcService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("testCycle")
@Tag(name = "测试周期", description = "测试周期相关接口")
@Slf4j
public class TestCycleController extends BaseController {

    @Resource
    private TestCycleService testCycleService;

    @Resource
    private TestCaseService testCaseService;

    @Resource
    private TestCycleJoinTestCaseService testCycleJoinTestCaseService;

    private TestCycleTcService testCycleTcService;

    @Autowired
    public void setTestCycleTcService(TestCycleTcService testCycleTcService) {
        this.testCycleTcService = testCycleTcService;
    }

//    @GetMapping("queryById/{id}")
//    @Operation(summary="查询测试周期")
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
//    @Operation(summary="测试周期")
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
    @PostMapping("caseRun/step")
    public Resp<String> runTestCycleTc(@RequestBody ExecuteTestCaseDto executeTestCaseDto){
        System.out.println(executeTestCaseDto.getTestCaseId());
        return testCycleTcService.runTestCycleTc(executeTestCaseDto);
    }

    @PostMapping("caseRun/execute")
    public Resp<PageInfo<Object>> runExecuteTestCase(@RequestBody ExecuteTestCaseRunDto executeTestCaseRunDto) {
        return testCycleTcService.runExecuteTestCase(executeTestCaseRunDto);
    }

    @PostMapping("caseRun/testCase")
    public Resp<String> runTestCase(@RequestBody TestCaseRunDto testCaseRunDto) throws ParseException {
        return testCycleTcService.runTestCase(testCaseRunDto);
    }
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
//    @Schema(description = "添加计划")
//    public Resp<String> addSchedule(@RequestBody TestCycleScheduleModel model) {
//        return testCycleService.addSchedule(model);
//    }

    @Operation(summary="列表")
    @PostMapping("/list")
    public Resp<PageInfo<TestCycle>> list(@RequestBody @Validated TestCycleParam param) {
        startPage();
        List<TestCycle> testCycleList = testCycleService.list(param);
        return new Resp.Builder<PageInfo<TestCycle>>().setData(PageInfo.of(testCycleList)).ok();
    }

    @Operation(summary="新增")
    @PostMapping("/saveTestCycle")
    public Resp<?> save(@RequestBody @Validated TestCycleSaveDto dto) {
        try {
            TestCycle testCycle = testCycleService.save(dto);
            if(Objects.isNull(testCycle)){
                return new Resp.Builder<TestCycle>().ok(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        SysConstantEnum.DATE_EXIST_TITLE.getValue(), HttpStatus.BAD_REQUEST.value());
            }
            return new Resp.Builder<TestCycle>().setData(testCycle).ok();
        } catch (Exception e) {
            log.error("新增测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCycle>().fail();
        }
    }

    @Operation(summary="修改")
    @PutMapping("/updateTestCycle")
    public Resp<?> update(@RequestBody @Validated TestCycleSaveDto dto) {
        try {
            TestCycle testCycle = testCycleService.update(dto);
            if(Objects.isNull(testCycle)){
                return new Resp.Builder<TestCycle>().ok( String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        SysConstantEnum.DATE_EXIST_TITLE.getValue(), HttpStatus.BAD_REQUEST.value());
            }
            return new Resp.Builder<TestCycle>().setData(testCycle).ok();
        } catch (Exception e) {
            log.error("修改测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCycle>().fail();
        }
    }

    @Operation(summary="详情")
    @GetMapping("/info/{id}")
    public Resp<TestCycle> info(@PathVariable Long id) {
        TestCycle testCycle = testCycleService.info(id);
        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
    }

    @Operation(summary="删除")
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

    @Operation(summary="克隆")
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

    @Operation(summary="列表")
    @PostMapping("/instance/listByTestCycle")
    public Resp<PageInfo<TestCaseBisDto>> listByTestCycle(@RequestBody TestCycleParam param) {
        if (null == param.getTestCycleId()) {
            throw new BaseException("测试周期ID不能为空");
        }
        /*** TestCaseParam tmpParam = new TestCaseParam();
         List<Long> caseIdList = this.testCycleJoinTestCaseService.getCaseIdListByCycleId(param.getTestCycleId());
         if (CollUtil.isEmpty(caseIdList)) {
         return new Resp.Builder<PageInfo<TestCase>>().setData(PageInfo.of(Collections.EMPTY_LIST)).ok();
         }
         tmpParam.setTestCaseIdList(caseIdList);
         startPage();
         List<TestCase> testCaseList = testCaseService.listExtend(tmpParam);
         **/
        List<TestCaseBisDto> testCaseAllByCycleId = testCaseService.getTestCaseAllByCycleId(param.getTestCycleId());
        startPage();
        return new Resp.Builder<PageInfo<TestCaseBisDto>>().setData(PageInfo.of(testCaseAllByCycleId)).ok();
    }


    @Operation(summary="绑定测试用例到测试周期")
    @PostMapping("/instance/saveInstance")
    public Resp<?> saveInstance(
        @RequestBody @Validated TestCycleJoinTestCaseSaveDto dto) {
        try {
            if (ArrayUtil.isEmpty(dto.getTestCaseIds())) {
                throw new BaseException("请选择至少一个测试用例进行绑定");
            }
            return new Resp.Builder<Boolean>().setData(testCycleJoinTestCaseService.saveInstance(dto)).ok();
        } catch (Exception e) {
            log.error("绑定测试用例到测试周期失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCycleJoinTestCase>().fail();
        }
    }

    @Operation(summary="删除测试周期绑定的测试用例")
    @DeleteMapping("/instance/deleteInstance")
    public Resp<?> deleteInstance(
        @RequestBody @Validated TestCycleJoinTestCaseSaveDto dto) {
        try {
            if (ArrayUtil.isEmpty(dto.getTestCaseIds())) {
                throw new BaseException("请选择至少一个测试用例进行删除");
            }
            testCycleJoinTestCaseService.deleteInstance(dto);
        } catch (Exception e) {
            log.error("删除测试周期绑定的测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Boolean>().fail();
        }
        return new Resp.Builder<Boolean>().ok();
    }



}
