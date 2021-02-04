package com.hu.oneclick.dao;

import com.hu.oneclick.dao.sql.FeatureSql;
import com.hu.oneclick.model.domain.Feature;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author qingyang
 */
public interface FeatureDao extends BaseMapper<Feature> {

    @Select(
            "select\n" +
                    "f.id as id,\n" +
                    "f.project_id as projectId,\n" +
                    "f.sprint_id as sprintId,\n" +
                    "f.epic as epic,\n" +
                    "f.report_to as reportTo,\n" +
                    "f.status as status,\n" +
                    "f.version as version,\n" +
                    "f.description as description,\n" +
                    "f.close_date as closeDate,\n" +
                    "f.author_name as authorName,\n" +
                    "f.create_time as createTime,\n" +
                    "f.title as title,\n" +
                    "f.update_time as updateTime,\n" +
                    "s.title as sprintTitle\n" +
                    "from feature f\n" +
                    "left join sprint s on f.sprint_id = s.id\n" +
                    "where f.id=#{id} and f.user_id = #{masterId}"
    )
    Feature queryById(@Param("id") String id,@Param("masterId") String masterId);

    @UpdateProvider(type = FeatureSql.class,method = "update")
    int update(Feature feature);
}
