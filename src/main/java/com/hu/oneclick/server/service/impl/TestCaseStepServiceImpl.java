package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.model.domain.dto.TestCaseStepSaveDto;
import com.hu.oneclick.model.domain.param.TestCaseStepParam;
import com.hu.oneclick.server.service.TestCaseStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author qingyang
 */
@Service
@Slf4j
public class TestCaseStepServiceImpl extends ServiceImpl<TestCaseStepDao, TestCaseStep> implements TestCaseStepService {

    @Override
    public List<TestCaseStep> list(TestCaseStepParam param) {
        return this.lambdaQuery()
                .like(StrUtil.isNotBlank(param.getTestStep()), TestCaseStep::getTestStep, param.getTestStep())
                .like(StrUtil.isNotBlank(param.getTestData()), TestCaseStep::getTestData, param.getTestData())
                .like(StrUtil.isNotBlank(param.getExpectedResult()), TestCaseStep::getExpectedResult, param.getExpectedResult())
                .list();
    }

    @Override
    public TestCaseStep save(TestCaseStepSaveDto dto) {
        TestCaseStep testCaseStep = new TestCaseStep();
        BeanUtil.copyProperties(dto, testCaseStep);
        baseMapper.insert(testCaseStep);
        return testCaseStep;
    }

    @Override
    public TestCaseStep update(TestCaseStepSaveDto dto) {
        TestCaseStep testCaseStep = baseMapper.selectById(dto.getId());
        if (testCaseStep == null) {
            throw new BaseException(StrUtil.format("测试用例步骤查询不到。ID：{}", dto.getId()));
        }
        BeanUtil.copyProperties(dto, testCaseStep);
        baseMapper.updateById(testCaseStep);
        return testCaseStep;
    }

    @Override
    public TestCaseStep info(Long id) {
        TestCaseStep testCaseStep = baseMapper.selectById(id);
        if (testCaseStep == null) {
            throw new BaseException(StrUtil.format("测试用例步骤查询不到。ID：{}", id));
        }
        return testCaseStep;
    }

}

