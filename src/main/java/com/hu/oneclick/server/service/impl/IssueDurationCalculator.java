
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

        // 数据库中存储的时间已经是UTC时间，直接使用，不需要再次转换
        Date adjustedCreateTime = issue.getCreateTime();
        
        System.out.println("=== 数据库中的createTime已经是UTC时间，直接使用: " + adjustedCreateTime + " ===");
        System.out.println("=== 不进行重复的UTC转换 ===");

        System.out.println("=== ========== Duration计算公式详情 ========== ===");
        System.out.println("=== 计算公式: duration(小时) = (当前UTC时间 - 创建UTC时间) / (1000 * 60 * 60) ===");
        System.out.println("=== 公式说明: 除以1000转换毫秒->秒，除以60转换秒->分钟，除以60转换分钟->小时 ===");
        System.out.println("=== 当前UTC时间: " + endTime + " ===");
        System.out.println("=== 当前UTC时间(毫秒): " + endTime.getTime() + " ===");
        System.out.println("=== 数据库UTC创建时间: " + adjustedCreateTime + " ===");
        System.out.println("=== 数据库UTC创建时间(毫秒): " + adjustedCreateTime.getTime() + " ===");

        long diffInMillis = endTime.getTime() - adjustedCreateTime.getTime();
        System.out.println("=== 时间差计算: " + endTime.getTime() + " - " + adjustedCreateTime.getTime() + " = " + diffInMillis + " 毫秒 ===");

        // 如果时间差为负数，说明可能存在时区问题或数据异常
        if (diffInMillis < 0) {
            System.out.println("=== 警告：时间差为负数，可能存在时区问题或数据异常 ===");
            System.out.println("=== 将使用绝对值计算duration ===");
            diffInMillis = Math.abs(diffInMillis);
        }

        // 转换为小时的详细计算过程
        double durationInSeconds = diffInMillis / 1000.0;
        double durationInMinutes = durationInSeconds / 60.0;
        double durationInHoursDouble = durationInMinutes / 60.0;
        int durationInHours = (int) durationInHoursDouble;
        
        System.out.println("=== Duration计算步骤: ===");
        System.out.println("=== 1. 毫秒转秒: " + diffInMillis + " / 1000 = " + durationInSeconds + " 秒 ===");
        System.out.println("=== 2. 秒转分钟: " + durationInSeconds + " / 60 = " + durationInMinutes + " 分钟 ===");
        System.out.println("=== 3. 分钟转小时: " + durationInMinutes + " / 60 = " + durationInHoursDouble + " 小时 ===");
        System.out.println("=== 4. 取整数部分: " + durationInHoursDouble + " -> " + durationInHours + " 小时 ===");
        System.out.println("=== 最终Duration结果: " + durationInHours + " 小时 ===");

        issue.setDuration(durationInHours);
        
        System.out.println("=== ========== Duration时间信息汇总 ========== ===");
        System.out.println("=== Issue ID: " + issue.getId() + " ===");
        System.out.println("=== 创建时间(原始): " + issue.getCreateTime() + " ===");
        System.out.println("=== 创建时间(调整后): " + adjustedCreateTime + " ===");
        System.out.println("=== 当前时间: " + endTime + " ===");
        System.out.println("=== 存活时长: " + durationInHours + " 小时 ===");
        System.out.println("=== 存活时长: " + durationInMinutes + " 分钟 ===");
        System.out.println("=== 存活时长: " + durationInSeconds + " 秒 ===");
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
     * @param userTimezone 用户时区（批量计算时不使用，因为数据库时间已是UTC）
     */
    private void calculateSingleIssueDuration(Issue issue, Date endTime, String userTimezone) {
        // 数据库中存储的时间已经是UTC时间，直接使用
        Date adjustedCreateTime = issue.getCreateTime();

        long diffInMillis = endTime.getTime() - adjustedCreateTime.getTime();

        // 处理负数情况
        if (diffInMillis < 0) {
            diffInMillis = Math.abs(diffInMillis);
        }

        // 转换为小时：除以(1000 * 60 * 60) = 除以3600000
        int durationInHours = (int) (diffInMillis / (1000 * 60 * 60));
        issue.setDuration(durationInHours);
    }
}
