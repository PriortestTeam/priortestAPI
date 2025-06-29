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
import java.util.Map;
import java.util.stream.Collectors;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.bean.BeanUtil;

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

    @Operation(summary = "获取测试用例列表")
    @PostMapping("/list")
    public Resp<PageInfo<TestCase>> list(@RequestBody Map<String, Object> param) {
        int pageNum = param.get("pageNum") != null ? Integer.parseInt(param.get("pageNum").toString()) : 1;
        int pageSize = param.get("pageSize") != null ? Integer.parseInt(param.get("pageSize").toString()) : 20;

        // 3. 子视图字段过滤参数
        if (param.containsKey("fieldNameEn") && param.containsKey("value") && param.containsKey("scopeName")) {
            String fieldNameEn = param.get("fieldNameEn").toString();
            String value = param.get("value").toString();
            String scopeName = param.get("scopeName").toString();
            String scopeId = param.get("scopeId") != null ? param.get("scopeId").toString() : null;
            PageInfo<TestCase> pageInfo = testCaseService.queryByFieldAndValue(fieldNameEn, value, scopeName, scopeId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<TestCase>>().setData(pageInfo).ok();
        }
        // 2. 视图过滤参数
        else if (param.containsKey("viewId") && param.get("viewId") != null && !param.get("viewId").toString().isEmpty()) {
            String viewId = param.get("viewId").toString();
            String projectId = param.get("projectId").toString();
            PageInfo<TestCase> pageInfo = testCaseService.listWithBeanSearcher(viewId, projectId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<TestCase>>().setData(pageInfo).ok();
        }
        // 1. 普通列表参数
        else if (param.containsKey("projectId")) {
            TestCaseParam testCaseParam = BeanUtil.toBean(param, TestCaseParam.class);
            PageInfo<TestCase> pageInfo = testCaseService.listWithViewFilter(testCaseParam, pageNum, pageSize);
            return new Resp.Builder<PageInfo<TestCase>>().setData(pageInfo).ok();
        } else {
            return new Resp.Builder<PageInfo<TestCase>>().fail();
        }
    }

    @Operation(summary = "新增测试用例")
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

    @Operation(summary = "修改测试用例")
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

    @Operation(summary = "获取测试用例列表")
    @GetMapping("/info/{id}")
    public Resp<TestCase> info(@PathVariable Long id) {
        TestCase testCase = testCaseService.info(id);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }


    @Operation(summary = "复制测试用例")
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

    @Operation(summary = "获取测试用例列表")
    @GetMapping("/testCaseSearch")
    public  Resp<List<TestCase>> testCaseSearch(@RequestParam  Long projectId, @RequestParam String title) {
        List<TestCase> testCaseList = testCaseService.testCaseSearch(projectId,title);
        return new Resp.Builder<List<TestCase>>().setData(testCaseList).ok();
    }

    @Operation(summary = "删除测试用例")
    @DeleteMapping("/delete/{id}")
    public Resp<?> delete(@PathVariable Long id) {
       return testCaseService.removeAndChild(id);
    }

}
