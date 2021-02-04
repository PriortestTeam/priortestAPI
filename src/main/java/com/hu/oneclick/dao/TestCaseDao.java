package com.hu.oneclick.dao;

import com.hu.oneclick.dao.sql.TestCaseSql;
import com.hu.oneclick.model.domain.TestCase;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author qingyang
 */
public interface TestCaseDao extends BaseMapper<TestCase> {


    @Select("select\n" +
            "id as id,\n" +
            "project_id as projectId,\n" +
            "title as title,\n" +
            "status as status,\n" +
            "start_date as startDate,\n" +
            "end_date as endDate,\n" +
            "description as description,\n" +
            "author_name as authorName,\n" +
            "create_time as createTime,\n" +
            "update_time as updateTime\n" +
            "from sprint where id = #{id} and user_id = #{masterId}")
    TestCase queryById(@Param("id") String id, @Param("masterId")String masterId);


    @UpdateProvider(type = TestCaseSql.class,method = "update")
    int update(TestCase testCase);

}
