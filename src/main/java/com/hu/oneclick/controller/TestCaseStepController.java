package com.hu.oneclick.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCaseStep;
import com.hu.oneclick.model.domain.dto.TestCaseStepSaveDto;
import com.hu.oneclick.model.param.TestCaseStepParam;
import com.hu.oneclick.relation.enums.RelationCategoryEnum;
import com.hu.oneclick.relation.service.RelationService;
import com.hu.oneclick.server.service.TestCaseStepService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例步骤控制器
 *
 * @author xiaohai
 * @date 2023/04/09
 */
@RestController
@RequestMapping("testCase/step")
@Slf4j
@Tag(name = "测试用例步骤", description = "测试用例步骤相关接口")
public class TestCaseStepController extends BaseController {

    @Resource
    private TestCaseStepService testCaseStepService;
    @Resource
    private RelationService relationService;

    @Operation"列表"
    @PostMapping("/list")
    public Resp<PageInfo<TestCaseStep>> list(@RequestBody TestCaseStepParam param) {
        startPage();
        List<TestCaseStep> testCaseStepList = testCaseStepService.list(param);
        return new Resp.Builder<PageInfo<TestCaseStep>>().setData(PageInfo.of(testCaseStepList)).ok();
    }

    @Operation"新增"
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

    @Operation"修改"
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

    @Operation"详情"
    @GetMapping("/info/{id}")
    public Resp<TestCaseStep> info(@PathVariable Long id) {
        TestCaseStep testCase = testCaseStepService.info(id);
        return new Resp.Builder<TestCaseStep>().setData(testCase).ok();
    }

    @Operation"删除"
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

    @Operation"查询测试用例关联的所有测试用例步骤"
    @GetMapping("/of/testCase/{testCaseId}")
    public Resp<List<TestCaseStep>> getTestCaseSteps(@PathVariable Long testCaseId) {
//        return new Resp.Builder<List<TestCaseStep>>().setData(testCaseStepService.lambdaQuery().eq(TestCaseStep::getTestCaseId, testCaseId).list()).ok();
        List<String> testCaseStepIdList = relationService.getRelationTargetIdListByObjectIdAndCategory(testCaseId.toString(), RelationCategoryEnum.TEST_CASE_TO_STEP.getValue());
        if (CollUtil.isEmpty(testCaseStepIdList)) {
            return new Resp.Builder<List<TestCaseStep>>().setData(ListUtil.list(false)).ok();
        }
        return new Resp.Builder<List<TestCaseStep>>().setData(testCaseStepService.lambdaQuery().in(TestCaseStep::getId, testCaseStepIdList.stream().map(Long::valueOf).collect(Collectors.toList())).list()).ok();
    }

}
