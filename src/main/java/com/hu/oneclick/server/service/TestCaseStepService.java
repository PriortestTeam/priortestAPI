package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.entity.TestCaseStep;
import com.hu.oneclick.model.domain.dto.TestCaseStepSaveDto;
import com.hu.oneclick.model.param.TestCaseStepParam;

import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseStepService extends IService<TestCaseStep> {

    List<TestCaseStep> list(TestCaseStepParam param);

    void save(TestCaseStepSaveDto dto);

    void update(TestCaseStepSaveDto dto);

    TestCaseStep info(Long id);

}
