
package com.hu.oneclick.server.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.model.entity.Issue;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Component
public class IssueTimeConverter {

    /**
     * 转换所有日期字段到UTC
     */
    public void convertDatesToUTC(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 没有用户时区信息，跳过UTC转换 ===");
            return;
        }

        System.out.println("=== 开始UTC转换，用户时区: " + userTimezone + " ===");

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

            // 转换 createTime（如果是新建时，通常由数据库自动设置）
            if (issue.getCreateTime() != null) {
                Date originalTime = issue.getCreateTime();
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setCreateTime(utcTime);
                System.out.println("=== createTime转换: " + originalTime + " -> " + utcTime + " ===");
            }

            // 转换 planFixDate
            if (issue.getPlanFixDate() != null) {
                Date originalTime = issue.getPlanFixDate();
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setPlanFixDate(utcTime);
                System.out.println("=== planFixDate转换: " + originalTime + " -> " + utcTime + " ===");
            }

            // 转换 issueExpand 中 attributes 里的日期字段
            convertAttributesDateFieldsToUTC(issue, userTZ);

        } catch (Exception e) {
            System.out.println("=== UTC转换失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 将UTC时间转换为用户本地时间（用于返回给前端显示）
     */
    public void convertUTCToLocalTime(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 没有用户时区信息，跳过本地时间转换 ===");
            return;
        }

        System.out.println("=== 开始UTC到本地时间转换，用户时区: " + userTimezone + " ===");

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

            // 转换 createTime
            if (issue.getCreateTime() != null) {
                Date originalTime = issue.getCreateTime();
                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                issue.setCreateTime(localTime);
                System.out.println("=== createTime转换: " + originalTime + " -> " + localTime + " ===");
            }

            // 转换 planFixDate
            if (issue.getPlanFixDate() != null) {
                Date originalTime = issue.getPlanFixDate();
                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                issue.setPlanFixDate(localTime);
                System.out.println("=== planFixDate转换: " + originalTime + " -> " + localTime + " ===");
            }

            // 转换 issueExpand 中 attributes 里的日期字段
            convertAttributesDateFieldsToLocalTime(issue, userTZ);

        } catch (Exception e) {
            System.out.println("=== UTC到本地时间转换失败: " + e.getMessage() + " ===");
        }
    }

    private void convertAttributesDateFieldsToUTC(Issue issue, TimeZone userTZ) {
        if (issue.getIssueExpand() == null || issue.getIssueExpand().isEmpty()) {
            return;
        }

        try {
            JSONObject issueExpandJson = JSONUtil.parseObj(issue.getIssueExpand());
            JSONArray attributes = issueExpandJson.getJSONArray("attributes");

            if (attributes != null && !attributes.isEmpty()) {
                for (Object attributeObj : attributes) {
                    if (attributeObj instanceof JSONObject) {
                        JSONObject attribute = (JSONObject) attributeObj;
                        String fieldType = attribute.getStr("fieldType");
                        String valueData = attribute.getStr("valueData");

                        if ("date".equals(fieldType) && valueData != null && !valueData.isEmpty()) {
                            try {
                                Date originalTime = cn.hutool.core.date.DateUtil.parse(valueData);
                                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                                attribute.set("valueData", cn.hutool.core.date.DateUtil.format(utcTime, "yyyy-MM-dd HH:mm:ss"));
                                System.out.println("=== Attribute日期字段转换: " + originalTime + " -> " + utcTime + " ===");
                            } catch (Exception e) {
                                System.out.println("=== Attribute日期字段转换失败: " + e.getMessage() + " ===");
                            }
                        }
                    }
                }
                issue.setIssueExpand(issueExpandJson.toString());
            }
        } catch (Exception e) {
            System.out.println("=== 处理attributes日期字段失败: " + e.getMessage() + " ===");
        }
    }

    private void convertAttributesDateFieldsToLocalTime(Issue issue, TimeZone userTZ) {
        if (issue.getIssueExpand() == null || issue.getIssueExpand().isEmpty()) {
            return;
        }

        try {
            JSONObject issueExpandJson = JSONUtil.parseObj(issue.getIssueExpand());
            JSONArray attributes = issueExpandJson.getJSONArray("attributes");

            if (attributes != null && !attributes.isEmpty()) {
                for (Object attributeObj : attributes) {
                    if (attributeObj instanceof JSONObject) {
                        JSONObject attribute = (JSONObject) attributeObj;
                        String fieldType = attribute.getStr("fieldType");
                        String valueData = attribute.getStr("valueData");

                        if ("date".equals(fieldType) && valueData != null && !valueData.isEmpty()) {
                            try {
                                Date originalTime = cn.hutool.core.date.DateUtil.parse(valueData);
                                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                                attribute.set("valueData", cn.hutool.core.date.DateUtil.format(localTime, "yyyy-MM-dd HH:mm:ss"));
                                System.out.println("=== Attribute日期字段转换: " + originalTime + " -> " + localTime + " ===");
                            } catch (Exception e) {
                                System.out.println("=== Attribute日期字段转换失败: " + e.getMessage() + " ===");
                            }
                        }
                    }
                }
                issue.setIssueExpand(issueExpandJson.toString());
            }
        } catch (Exception e) {
            System.out.println("=== 处理attributes日期字段失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 将用户本地时间转换为UTC时间
     */
    private Date convertLocalTimeToUTC(Date localTime, TimeZone userTZ) {
        // 将本地时间解释为用户时区的时间，然后转换为UTC
        Calendar localCalendar = Calendar.getInstance(userTZ);
        localCalendar.setTime(localTime);

        // 获取UTC时间
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCalendar.setTimeInMillis(localCalendar.getTimeInMillis());

        return utcCalendar.getTime();
    }

    /**
     * 将UTC时间转换为用户本地时间
     */
    private Date convertUTCToLocalTime(Date utcTime, TimeZone userTZ) {
        // 将UTC时间解释为UTC时区的时间，然后转换为用户时区
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCalendar.setTime(utcTime);

        // 获取用户本地时间
        Calendar localCalendar = Calendar.getInstance(userTZ);
        localCalendar.setTimeInMillis(utcCalendar.getTimeInMillis());

        return localCalendar.getTime();
    }
}
