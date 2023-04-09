package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.model.domain.dto.TestCaseStepSaveDto;
import com.hu.oneclick.model.domain.param.TestCaseStepParam;
import com.hu.oneclick.server.service.TestCaseStepService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 测试用例步骤控制器
 *
 * @author xiaohai
 * @date 2023/04/09
 */
@RestController
@RequestMapping("testCase/step")
@Slf4j
@Api(tags = "测试用例步骤")
public class TestCaseStepController extends BaseController {

    @Resource
    private TestCaseStepService testCaseStepService;

    @ApiOperation("列表")
    @PostMapping("/list")
    public Resp<PageInfo<TestCaseStep>> list(@RequestBody TestCaseStepParam param) {
        startPage();
        List<TestCaseStep> testCaseStepList = testCaseStepService.list(param);
        return new Resp.Builder<PageInfo<TestCaseStep>>().setData(PageInfo.of(testCaseStepList)).ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated TestCaseStepSaveDto dto) {
        try {
            testCaseStepService.save(dto);
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("新增测试用例步骤失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public Resp<?> update(@RequestBody @Validated TestCaseStepSaveDto dto) {
        try {
            testCaseStepService.update(dto);
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("修改测试用例步骤失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public Resp<TestCaseStep> info(@PathVariable Long id) {
        TestCaseStep testCase = testCaseStepService.info(id);
        return new Resp.Builder<TestCaseStep>().setData(testCase).ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            testCaseStepService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除测试用例步骤失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCaseStep>().fail();
        }
        return new Resp.Builder<TestCaseStep>().ok();
    }

}
