package com.hu.oneclick.dao.sql;

import com.hu.oneclick.model.domain.Sprint;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author qingyang
 */
public class SprintSql {


    public String update(final Sprint sprint){
        return new SQL(){{
            UPDATE("sprint");
            if (StringUtils.isNotEmpty(sprint.getTitle())){
                SET("title=#{title}");
            }
            if (sprint.getStatus() != null){
                SET("status=#{status}");
            }
            if (sprint.getStartDate() != null){
                SET("start_date=#{startDate}");
            }
            if (sprint.getEndDate() != null){
                SET("end_date=#{endDate}");
            }
            if (StringUtils.isNotEmpty(sprint.getDescription())){
                SET("description=#{description}");
            }
            if (StringUtils.isNotEmpty(sprint.getAuthorName())){
                SET("author_name=#{authorName}");
            }
            SET("update_time=NOW()");
            WHERE("id = #{id}");
        }}.toString();
    }

}
