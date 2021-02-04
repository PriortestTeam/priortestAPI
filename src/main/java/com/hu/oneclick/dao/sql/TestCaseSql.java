package com.hu.oneclick.dao.sql;

import com.hu.oneclick.model.domain.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author qingyang
 */
public class TestCaseSql {

    public String update(TestCase testCase){
        return new SQL(){{
            UPDATE("test_case");
            if (StringUtils.isNotEmpty(testCase.getTitle())){
                SET("title=#{title}");
            }
            if (StringUtils.isNotEmpty(testCase.getPriority())){
                SET("priority=#{priority}");
            }
            if (StringUtils.isNotEmpty(testCase.getFeature())){
                SET("feature=#{feature}");
            }
            if (StringUtils.isNotEmpty(testCase.getDescription())){
                SET("description=#{description}");
            }
            if (testCase.getStatus() != null){
                SET("status=#{status}");
            }
            if (testCase.getExecutedDate() != null){
                SET("executed_date=#{executedDate}");
            }
            if (StringUtils.isNotEmpty(testCase.getAuthorName())){
                SET("author_name=#{authorName}");
            }
            SET("update_time=NOW()");
            WHERE("id = #{id}");
        }}.toString();
    }



}
