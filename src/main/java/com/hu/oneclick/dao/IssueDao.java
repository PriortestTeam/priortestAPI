
package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.Issue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IssueDao extends BaseMapper<Issue> {
    
    List<Issue> queryList(Issue issue);
    
    int updateBatch(@Param("list") List<Issue> list);
}
