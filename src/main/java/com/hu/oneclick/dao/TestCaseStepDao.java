package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCaseStep;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseStepDao extends BaseMapper<TestCaseStep> {

    int update(TestCaseStep testCaseStep);

    List<TestCaseStep> queryList(TestCaseStep testCaseStep);
}
