package com.hu.oneclick.dao.sql;

import com.hu.oneclick.model.domain.TestCaseStep;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author qingyang
 */
public class TestCaseStepSql {

    public String update(TestCaseStep testCaseStep){
        return new SQL(){{
            UPDATE("step");
            if (StringUtils.isNotEmpty(testCaseStep.getStep())){
                SET("step=#{step}");
            }
            if (StringUtils.isNotEmpty(testCaseStep.getExpectedResult())){
                SET("expected_result=#{expectedResult}");
            }
            if (testCaseStep.getStatus() != null){
                SET("status=#{status}");
            }
            if (testCaseStep.getTestDate() != null){
                SET("test_date=#{testDate}");
            }
            WHERE("id = #{id} and test_case_id = #{testCaseId}");
        }}.toString();
    }



}
