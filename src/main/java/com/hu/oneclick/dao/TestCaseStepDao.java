package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.TestCaseStep;
import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseStepDao extends BaseMapper<TestCaseStep> {

    int updateByPrimaryKeySelective(TestCaseStep testCaseStep);

    List<TestCaseStep> queryList(TestCaseStep testCaseStep);
}
