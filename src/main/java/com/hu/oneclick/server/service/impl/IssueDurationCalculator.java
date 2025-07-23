
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.model.entity.Issue;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class IssueDurationCalculator {

    /**
     * 计算单个Issue的duration（存活时长）
     * 基于UTC时间进行计算，避免时区问题
     * 
     * @param issue Issue对象
     */
    public void calculateDuration(Issue issue) {
        calculateDuration(issue, null);
    }

    /**
     * 计算单个Issue的duration，考虑用户时区
     * 
     * @param issue Issue对象
     * @param userTimezone 用户时区
     */
    public void calculateDuration(Issue issue, String userTimezone) {
        System.out.println("=== IssueDurationCalculator - Duration计算开始 ===");
        System.out.println("=== Issue ID: " + issue.getId() + " ===");
        System.out.println("=== 用户时区: " + userTimezone + " ===");

        if (issue.getCreateTime() == null) {
            System.out.println("=== createTime为null，无法计算duration ===");
            issue.setDuration(0);
            return;
        }

        // 统一使用UTC时间进行计算，避免时区问题
        Date endTime;
        // 获取UTC当前时间作为结束时间
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endTime = utcCalendar.getTime();
        System.out.println("=== 使用当前UTC时间计算duration ===");

        // 处理数据库中存储的时间
        Date adjustedCreateTime = issue.getCreateTime();

        // 如果数据库中存储的时间是用户本地时区时间，需要转换为UTC
        if (userTimezone != null && !userTimezone.isEmpty()) {
            try {
                TimeZone userTZ = TimeZone.getTimeZone(userTimezone);
                TimeZone utcTZ = TimeZone.getTimeZone("UTC");

                // 如果数据库存储的是用户本地时间，需要转换为UTC
                // 这里假设数据库存储的是用户创建时的本地时间
                Calendar userCalendar = Calendar.getInstance(userTZ);
                userCalendar.setTime(issue.getCreateTime());

                // 转换为UTC时间
                long utcTime = userCalendar.getTimeInMillis() - userTZ.getOffset(userCalendar.getTimeInMillis()) + utcTZ.getOffset(userCalendar.getTimeInMillis());
                adjustedCreateTime = new Date(utcTime);

                System.out.println("=== 原始创建时间: " + issue.getCreateTime() + " ===");
                System.out.println("=== 调整后UTC创建时间: " + adjustedCreateTime + " ===");
            } catch (Exception e) {
                System.out.println("=== 时区转换失败，使用原始时间: " + e.getMessage() + " ===");
                adjustedCreateTime = issue.getCreateTime();
            }
        }

        System.out.println("=== 当前UTC时间: " + endTime + " ===");
        System.out.println("=== 当前UTC时间(毫秒): " + endTime.getTime() + " ===");
        System.out.println("=== 最终创建时间: " + adjustedCreateTime + " ===");
        System.out.println("=== 最终创建时间(毫秒): " + adjustedCreateTime.getTime() + " ===");

        long diffInMillis = endTime.getTime() - adjustedCreateTime.getTime();
        System.out.println("=== 时间差(毫秒): " + diffInMillis + " ===");

        // 如果时间差为负数，说明可能存在时区问题或数据异常
        if (diffInMillis < 0) {
            System.out.println("=== 警告：时间差为负数，可能存在时区问题或数据异常 ===");
            System.out.println("=== 将使用绝对值计算duration ===");
            diffInMillis = Math.abs(diffInMillis);
        }

        // 转换为小时
        int durationInHours = (int) (diffInMillis / (1000 * 60 * 60));
        System.out.println("=== 计算得到duration: " + durationInHours + " 小时 ===");

        issue.setDuration(durationInHours);
        System.out.println("=== IssueDurationCalculator - Duration计算完成 ===");
    }

    /**
     * 批量计算Issue列表的duration，提高性能
     * 
     * @param issues Issue列表
     */
    public void calculateDurationForList(List<Issue> issues) {
        calculateDurationForList(issues, null);
    }

    /**
     * 批量计算Issue列表的duration，考虑用户时区
     * 
     * @param issues Issue列表
     * @param userTimezone 用户时区
     */
    public void calculateDurationForList(List<Issue> issues, String userTimezone) {
        System.out.println("=== IssueDurationCalculator - 批量计算duration开始，数量: " + issues.size() + " ===");
        
        if (issues == null || issues.isEmpty()) {
            System.out.println("=== Issue列表为空，跳过duration计算 ===");
            return;
        }

        // 获取UTC当前时间，批量计算时统一使用同一个时间点
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date currentUtcTime = utcCalendar.getTime();
        System.out.println("=== 批量计算使用的UTC时间: " + currentUtcTime + " ===");

        for (Issue issue : issues) {
            if (issue.getCreateTime() != null) {
                calculateSingleIssueDuration(issue, currentUtcTime, userTimezone);
            } else {
                System.out.println("=== Issue ID " + issue.getId() + " createTime为null，设置duration为0 ===");
                issue.setDuration(0);
            }
        }

        System.out.println("=== IssueDurationCalculator - 批量计算duration完成 ===");
    }

    /**
     * 计算单个Issue的duration的内部方法
     * 
     * @param issue Issue对象
     * @param endTime 结束时间
     * @param userTimezone 用户时区
     */
    private void calculateSingleIssueDuration(Issue issue, Date endTime, String userTimezone) {
        Date adjustedCreateTime = issue.getCreateTime();

        // 如果需要时区转换
        if (userTimezone != null && !userTimezone.isEmpty()) {
            try {
                TimeZone userTZ = TimeZone.getTimeZone(userTimezone);
                TimeZone utcTZ = TimeZone.getTimeZone("UTC");

                Calendar userCalendar = Calendar.getInstance(userTZ);
                userCalendar.setTime(issue.getCreateTime());

                long utcTime = userCalendar.getTimeInMillis() - userTZ.getOffset(userCalendar.getTimeInMillis()) + utcTZ.getOffset(userCalendar.getTimeInMillis());
                adjustedCreateTime = new Date(utcTime);
            } catch (Exception e) {
                System.out.println("=== Issue ID " + issue.getId() + " 时区转换失败: " + e.getMessage() + " ===");
                adjustedCreateTime = issue.getCreateTime();
            }
        }

        long diffInMillis = endTime.getTime() - adjustedCreateTime.getTime();

        // 处理负数情况
        if (diffInMillis < 0) {
            diffInMillis = Math.abs(diffInMillis);
        }

        // 转换为小时
        int durationInHours = (int) (diffInMillis / (1000 * 60 * 60));
        issue.setDuration(durationInHours);
    }
}
