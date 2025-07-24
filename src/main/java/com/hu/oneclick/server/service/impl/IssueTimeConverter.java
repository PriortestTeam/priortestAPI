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
     * 只转换主要时间字段到UTC（createTime, updateTime, planFixDate）
     */
    public void convertMainTimeFieldsToUTC(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 没有用户时区信息，跳过主要字段UTC转换 ===");
            return;
        }

        System.out.println("=== 开始主要字段UTC转换，用户时区: " + userTimezone + " ===");

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

            // 转换 createTime（如果是新建时，通常由数据库自动设置）
            if (issue.getCreateTime() != null) {
                Date originalTime = issue.getCreateTime();
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setCreateTime(utcTime);
                System.out.println("=== createTime转换: " + originalTime + " -> " + utcTime + " ===");
            }

            // 转换 updateTime（通常由数据库自动设置）
            if (issue.getUpdateTime() != null) {
                Date originalTime = issue.getUpdateTime();
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setUpdateTime(utcTime);
                System.out.println("=== updateTime转换: " + originalTime + " -> " + utcTime + " ===");
            }

            // 转换 planFixDate
            if (issue.getPlanFixDate() != null) {
                System.out.println("=== 开始转换日期型字段 planFixDate ===");
                Date originalTime = issue.getPlanFixDate();
                System.out.println("=== planFixDate 字段原始时间: " + originalTime + " ===");
                System.out.println("=== planFixDate 字段原始时间(毫秒): " + originalTime.getTime() + " ===");
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setPlanFixDate(utcTime);
                System.out.println("=== planFixDate 字段转换成UTC时间: " + utcTime + " ===");
                System.out.println("=== planFixDate 字段转换成UTC时间(毫秒): " + utcTime.getTime() + " ===");
                System.out.println("=== planFixDate 字段转换完成 ===");
            }

        } catch (Exception e) {
            System.out.println("=== 主要字段UTC转换失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 只转换自定义字段中的日期到UTC（issueExpand中的attributes）
     */
    public void convertCustomFieldsTimeToUTC(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 没有用户时区信息，跳过自定义字段UTC转换 ===");
            return;
        }

        System.out.println("=== 开始自定义字段UTC转换，用户时区: " + userTimezone + " ===");

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

            // 只转换 issueExpand 中 attributes 里的日期字段
            convertAttributesDateFieldsToUTC(issue, userTZ);

        } catch (Exception e) {
            System.out.println("=== 自定义字段UTC转换失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 转换所有日期字段到UTC（保持向后兼容）
     */
    public void convertUserInputTimeToUTC(Issue issue, String userTimezone) {
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
                System.out.println("=== 开始转换日期型字段 planFixDate ===");
                Date originalTime = issue.getPlanFixDate();
                System.out.println("=== planFixDate 字段原始时间: " + originalTime + " ===");
                System.out.println("=== planFixDate 字段原始时间(毫秒): " + originalTime.getTime() + " ===");
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                System.out.println("=== planFixDate 字段转换成UTC时间: " + utcTime + " ===");
                System.out.println("=== planFixDate 字段转换成UTC时间(毫秒): " + utcTime.getTime() + " ===");
                issue.setPlanFixDate(utcTime);
                System.out.println("=== planFixDate 字段转换完成 ===");
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

            // 转换 updateTime
            if (issue.getUpdateTime() != null) {
                Date originalTime = issue.getUpdateTime();
                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                issue.setUpdateTime(localTime);
                System.out.println("=== updateTime转换: " + originalTime + " -> " + localTime + " ===");
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
        // 获取用户时区的偏移量（毫秒）
        long offsetMillis = userTZ.getOffset(localTime.getTime());

        // 用户本地时间 - 时区偏移 = UTC时间
        long utcTimeMillis = localTime.getTime() - offsetMillis;

        Date utcTime = new Date(utcTimeMillis);

        System.out.println("=== convertLocalTimeToUTC详细转换 ===");
        System.out.println("=== 输入本地时间: " + localTime + " ===");
        System.out.println("=== 用户时区: " + userTZ.getID() + " ===");
        System.out.println("=== 时区偏移毫秒: " + offsetMillis + " ===");
        System.out.println("=== 时区偏移小时: " + (offsetMillis / (1000 * 60 * 60)) + " ===");
        System.out.println("=== 本地时间毫秒: " + localTime.getTime() + " ===");
        System.out.println("=== UTC时间毫秒: " + utcTimeMillis + " ===");
        System.out.println("=== 转换后UTC时间: " + utcTime + " ===");

        return utcTime;
    }

    /**
     * 将UTC时间转换为用户本地时间的工具方法
     */
    private Date convertUTCToLocalTime(Date utcTime, TimeZone userTZ) {
        System.out.println("=== convertUTCToLocalTime开始 ===");
        System.out.println("=== 输入UTC时间: " + utcTime + " ===");
        System.out.println("=== 目标用户时区: " + userTZ.getID() + " ===");

        // 获取UTC时间的毫秒数
        long utcMillis = utcTime.getTime();
        System.out.println("=== UTC时间毫秒数: " + utcMillis + " ===");

        // 获取用户时区相对于UTC的偏移量
        int offsetInMillis = userTZ.getOffset(utcMillis);
        System.out.println("=== 时区偏移毫秒数: " + offsetInMillis + " ===");
        System.out.println("=== 时区偏移小时数: " + (offsetInMillis / (1000 * 60 * 60)) + " ===");

        // 添加偏移量得到本地时间
        long localMillis = utcMillis + offsetInMillis;
        Date localTime = new Date(localMillis);

        System.out.println("=== 转换后本地时间: " + localTime + " ===");
        System.out.println("=== convertUTCToLocalTime结束 ===");

        return localTime;
    }
}