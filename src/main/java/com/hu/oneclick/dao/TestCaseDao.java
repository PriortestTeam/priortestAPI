package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface TestCaseDao extends BaseMapper<TestCase> {


    TestCase queryById(@Param("id") String id, @Param("masterId")String masterId);

    int update(TestCase testCase);


    List<LeftJoinDto> queryTitles(@Param("projectId")String projectId, @Param("title") String title, @Param("masterId") String masterId);

}
