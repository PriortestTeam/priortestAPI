package com.hu.oneclick.dao.sql;

import com.hu.oneclick.model.domain.Feature;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author qingyang
 */
public class FeatureSql {

    public String update(Feature feature){
        return new SQL(){{
            UPDATE("feature");
            if (StringUtils.isNotEmpty(feature.getTitle())){
                SET("title=#{title}");
            }
            if (StringUtils.isNotEmpty(feature.getSprintId())){
                SET("sprint_id=#{sprintId}");
            }
            if (StringUtils.isNotEmpty(feature.getEpic())){
                SET("epic=#{epic}");
            }
            if (StringUtils.isNotEmpty(feature.getReportTo())){
                SET("report_to=#{reportTo}");
            }
            if (feature.getStatus() != null){
                SET("status=#{status}");
            }
            if (StringUtils.isNotEmpty(feature.getVersion())){
                SET("version=#{version}");
            }
            if (StringUtils.isNotEmpty(feature.getDescription())){
                SET("description=#{description}");
            }
            if (StringUtils.isNotEmpty(feature.getAuthorName())){
                SET("author_name=#{authorName}");
            }
            if (feature.getCloseDate() != null){
                SET("close_date=#{closeDate}");
            }
            SET("update_time=NOW()");
            WHERE("id = #{id}");
        }}.toString();
    }


}
