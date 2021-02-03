package com.hu.oneclick.dao;

import com.hu.oneclick.dao.sql.SprintSql;
import com.hu.oneclick.model.domain.Sprint;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author qingyang
 */
public interface SprintDao extends BaseMapper<Sprint> {



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
    Sprint queryById(@Param("id") String id,@Param("masterId")String masterId);


    @UpdateProvider(type = SprintSql.class,method = "update")
    int update(Sprint sprint);

}
