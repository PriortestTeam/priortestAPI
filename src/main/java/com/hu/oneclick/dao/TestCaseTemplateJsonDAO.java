package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.TestCaseTemplateJson;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * TestCaseTemplateJsonDAO继承基类
 */
public interface TestCaseTemplateJsonDAO extends BaseMapper<TestCaseTemplateJson> {


    List<TestCaseTemplateJson> queryByUserId(@Param("masterId") String masterId);

    int insertOne(TestCaseTemplateJson testCaseTemplateJson);


    int updateByPrimaryKeySelective(TestCaseTemplateJson testCaseTemplateJson);

    int deleteById(String id);

    TestCaseTemplateJson selectByPrimaryKey(@Param("id") String id);
}
