package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCaseStep;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author qingyang
 */
public interface TestCaseStepDao extends BaseMapper<TestCaseStep> {

    int update(TestCaseStep testCaseStep);

}
