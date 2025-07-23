
package com.hu.oneclick.server.service.impl.issue;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hu.oneclick.model.entity.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.TimeZone;

/**
 * Issue时间转换工具类
 */
@Slf4j
@Component
public class IssueTimeConverter {

    /**
     * 将Issue中的时间字段从UTC转换为用户时区
     */
    public void convertIssueTimeToUserTZ(Issue issue, String userTimezone) {
        if (issue == null || StrUtil.isBlank(userTimezone)) {
            return;
        }

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);
            
            // 转换计划修复时间
            if (issue.getPlanFixDate() != null) {
                issue.setPlanFixDate(convertUTCToUserTZ(issue.getPlanFixDate(), userTZ));
            }

            // 转换创建时间
            if (issue.getCreateTime() != null) {
                issue.setCreateTime(convertUTCToUserTZ(issue.getCreateTime(), userTZ));
            }

            // 转换更新时间
            if (issue.getUpdateTime() != null) {
                issue.setUpdateTime(convertUTCToUserTZ(issue.getUpdateTime(), userTZ));
            }

        } catch (Exception e) {
            log.warn("时区转换失败，使用原始时间: {}", e.getMessage());
        }
    }

    /**
     * 将Issue中的时间字段从用户时区转换为UTC
     */
    public void convertIssueTimeToUTC(Issue issue, String userTimezone) {
        if (issue == null || StrUtil.isBlank(userTimezone)) {
            return;
        }

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);
            
            // 转换计划修复时间
            if (issue.getPlanFixDate() != null) {
                issue.setPlanFixDate(convertUserTZToUTC(issue.getPlanFixDate(), userTZ));
            }

            // 注意：创建时间和更新时间通常由系统管理，不需要从用户时区转换

        } catch (Exception e) {
            log.warn("时区转换失败，使用原始时间: {}", e.getMessage());
        }
    }

    /**
     * 将UTC时间转换为用户时区
     */
    private Date convertUTCToUserTZ(Date utcDate, TimeZone userTZ) {
        if (utcDate == null) {
            return null;
        }
        
        // UTC时间戳
        long utcTime = utcDate.getTime();
        
        // 加上用户时区偏移量
        int offset = userTZ.getOffset(utcTime);
        return new Date(utcTime + offset);
    }

    /**
     * 将用户时区时间转换为UTC
     */
    private Date convertUserTZToUTC(Date userDate, TimeZone userTZ) {
        if (userDate == null) {
            return null;
        }
        
        // 用户时区时间戳
        long userTime = userDate.getTime();
        
        // 减去用户时区偏移量
        int offset = userTZ.getOffset(userTime);
        return new Date(userTime - offset);
    }
}
