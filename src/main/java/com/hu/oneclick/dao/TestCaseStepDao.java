package com.hu.oneclick.dao;

import com.hu.oneclick.dao.sql.TestCaseStepSql;
import com.hu.oneclick.model.domain.TestCaseStep;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author qingyang
 */
public interface TestCaseStepDao extends BaseMapper<TestCaseStep> {

    @UpdateProvider(type = TestCaseStepSql.class,method = "update")
    int update(TestCaseStep testCaseStep);

}
