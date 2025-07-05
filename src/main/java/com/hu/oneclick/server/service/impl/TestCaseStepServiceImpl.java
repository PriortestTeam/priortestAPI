package com.hu.oneclick.server.service.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.model.entity.TestCaseStep;
import com.hu.oneclick.model.domain.dto.TestCaseStepSaveDto;
import com.hu.oneclick.model.domain.dto.TestCaseStepSaveSubDto;
import com.hu.oneclick.model.param.TestCaseStepParam;
import com.hu.oneclick.relation.enums.RelationCategoryEnum;
import com.hu.oneclick.relation.service.RelationService;
import com.hu.oneclick.server.service.TestCaseStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author qingyang
 */
@Service
@Slf4j
public class TestCaseStepServiceImpl extends ServiceImpl<TestCaseStepDao, TestCaseStep> implements TestCaseStepService {
    @Resource
    private RelationService relationService;
    @Override
    public List<TestCaseStep> list(TestCaseStepParam param) {
        return this.lambdaQuery()
                .eq(param.getTestCaseId() != null, TestCaseStep::getTestCaseId, param.getTestCaseId()
                .like(StrUtil.isNotBlank(param.getTestStep(), TestCaseStep::getTestStep, param.getTestStep()
                .like(StrUtil.isNotBlank(param.getTestData(), TestCaseStep::getTestData, param.getTestData()
                .like(StrUtil.isNotBlank(param.getExpectedResult(), TestCaseStep::getExpectedResult, param.getExpectedResult()
                .list();
    }
    @Override
    public void save(TestCaseStepSaveDto dto) {
        List<TestCaseStep> testCaseStepList = new ArrayList<>();
        for (TestCaseStepSaveSubDto step : dto.getSteps() {
            TestCaseStep testCaseStep = new TestCaseStep();
            BeanUtil.copyProperties(step, testCaseStep);
            testCaseStep.setTestCaseId(dto.getTestCaseId();
            // 设置statusCode默认值
            testCaseStep.setStatusCode(5);
            // 保存自定义字段
            if (!JSONUtil.isNull(step.getCustomFieldDatas() {
                testCaseStep.setTeststepExpand(JSONUtil.toJsonStr(step.getCustomFieldDatas();
            }
            testCaseStepList.add(testCaseStep);
        }
        // 更新的测试用例步骤
        this.saveOrUpdateBatch(testCaseStepList);
        // 更新绑定关系
        List<String> testCaseStepIdList = testCaseStepList.stream().map(TestCaseStep::getId).map(String::valueOf).collect(Collectors.toList();
        relationService.saveRelationBatchWithClear(dto.getTestCaseId().toString(), testCaseStepIdList, RelationCategoryEnum.TEST_CASE_TO_STEP.getValue();
//        if (CollUtil.isEmpty(testCaseStepIdList) {
//            // 如果为空,说明需要删除该测试用例下的所有步骤
//            testCaseStepIdList.add(1L);
//        }
//        this.lambdaUpdate().eq(TestCaseStep::getTestCaseId, dto.getTestCaseId().notIn(TestCaseStep::getId, testCaseStepIdList).remove();
//        this.saveOrUpdateBatch(testCaseStepList);
    }
    @Override
    public void update(TestCaseStepSaveDto dto) {
        List<TestCaseStep> testCaseStepList = new ArrayList<>();
        for (TestCaseStepSaveSubDto step : dto.getSteps() {
            TestCaseStep testCaseStep = new TestCaseStep();
            BeanUtil.copyProperties(step, testCaseStep);
            testCaseStep.setTestCaseId(dto.getTestCaseId();
            // 设置statusCode默认值
            testCaseStep.setStatusCode(5);
            // 修改自定义字段
            if (!JSONUtil.isNull(step.getCustomFieldDatas() {
                testCaseStep.setTeststepExpand(JSONUtil.toJsonStr(step.getCustomFieldDatas();
            }
            testCaseStepList.add(testCaseStep);
        }
        this.saveOrUpdateBatch(testCaseStepList);
    }
    @Override
    public TestCaseStep info(Long id) {
        TestCaseStep testCaseStep = baseMapper.selectById(id);
        if (testCaseStep == null) {
            throw new BizException(StrUtil.format("测试用例步骤查询不到。ID：{}", id);
        }
        return testCaseStep;
    }
}
}
}
