package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.domain.dto.TestCaseSaveDto;
import com.hu.oneclick.model.param.TestCaseParam;
import com.hu.oneclick.server.service.TestCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "测试用例", description = "测试用例相关接口")
@Slf4j
public class TestCaseController extends BaseController {

    @Resource
    private TestCaseService testCaseService;

    //@GetMapping("queryById/{id}")
    //@Operation"查询测试用例"
    //public Resp<TestCase> queryById(@PathVariable Long id) {
    //    return testCaseService.queryById(id);
    //}
    //
    //@PostMapping("queryList")
    //@Operation"查询测试用例"
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
    //@Operation"添加测试用例"
    //public Resp<String> addTestCase(@RequestBody TestCycleDto testCycleDto) {
    //    return testCaseService.addTestCase(testCycleDto);
    //}
    //
    //@PostMapping("updateAction")
    //@Operation"更新action"
    //public Resp<List<TestCase>> updateAction(@RequestBody List<String> testCaseId, @RequestParam String actionType
    //        , @RequestParam String testCycleId) {
    //    return testCaseService.updateAction(testCaseId, actionType, testCycleId);
    //}

    @Operation"列表"
    @PostMapping("/list")
    public Resp<PageInfo<TestCase>> list(@RequestBody @Validated TestCaseParam param) {
        startPage();
        List<TestCase> testCaseList = testCaseService.list(param);
        return new Resp.Builder<PageInfo<TestCase>>().setData(PageInfo.of(testCaseList)).ok();
    }

    @Operation"新增"
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

    @Operation"修改"
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

    @Operation"详情"
    @GetMapping("/info/{id}")
    public Resp<TestCase> info(@PathVariable Long id) {
        TestCase testCase = testCaseService.info(id);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

//    @Operation"删除"
//    @DeleteMapping("/delete/{ids}")
//    public Resp<?> delete(@PathVariable Long[] ids) {
//        try {
//            testCaseService.removeByIds(Arrays.asList(ids));
//        } catch (Exception e) {
//            log.error("删除测试用例失败，原因：" + e.getMessage(), e);
//            return new Resp.Builder<TestCase>().fail();
//        }
//        return new Resp.Builder<TestCase>().ok();
//    }

    @Operation"克隆"
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            testCaseService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @Operation"测试用例输入框回显"
    @GetMapping("/testCaseSearch")
    public  Resp<List<TestCase>> testCaseSearch(@RequestParam  Long projectId, @RequestParam String title) {
        List<TestCase> testCaseList = testCaseService.testCaseSearch(projectId,title);
        return new Resp.Builder<List<TestCase>>().setData(testCaseList).ok();
    }

    @Operation"删除"
    @DeleteMapping("/delete/{id}")
    public Resp<?> delete(@PathVariable Long id) {
       return testCaseService.removeAndChild(id);
    }

}
