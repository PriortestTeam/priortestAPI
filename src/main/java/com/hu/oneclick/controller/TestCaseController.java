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
    public Resp<PageInfo<TestCase>> list(@RequestBody @Validated TestCaseParam param) {
        startPage();
        
        // 检查是否有viewId参数
        if (StrUtil.isNotBlank(param.getViewId())) {
            // 使用BeanSearcher查询，与BeanSearchController保持一致
            String projectId = param.getProjectId().toString();
            List<Map<String, Object>> resultList = testCaseService.listWithBeanSearcher(param.getViewId(), projectId);
            
            // 自动将Map转换为TestCase对象，所有字段自动赋值
            List<TestCase> testCaseList = resultList.stream()
                .map(map -> BeanUtil.toBeanIgnoreError(map, TestCase.class))
                .collect(Collectors.toList());
            
            return new Resp.Builder<PageInfo<TestCase>>().setData(PageInfo.of(testCaseList)).ok();
        } else {
            // 使用原有的查询逻辑
            List<TestCase> testCaseList = testCaseService.listWithViewFilter(param);
            return new Resp.Builder<PageInfo<TestCase>>().setData(PageInfo.of(testCaseList)).ok();
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
