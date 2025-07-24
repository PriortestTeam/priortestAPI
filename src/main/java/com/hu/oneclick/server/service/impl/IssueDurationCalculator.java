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
        System.out.println("=== ========================================== ===");
        System.out.println("=== IssueDurationCalculator.calculateDuration(issue, timezone) 方法开始 ===");
        System.out.println("=== ========================================== ===");
        System.out.println("=== 输入参数信息: ===");
        System.out.println("=== Issue ID: " + issue.getId() + " ===");
        System.out.println("=== Issue对象: " + issue + " ===");
        System.out.println("=== 用户时区参数: " + userTimezone + " ===");
        System.out.println("=== Issue createTime原始值: " + issue.getCreateTime() + " ===");

        if (issue.getCreateTime() == null) {
            System.out.println("=== ❌ createTime为null，无法计算duration，设置为0 ===");
            issue.setDuration(0);
            System.out.println("=== IssueDurationCalculator.calculateDuration() 方法结束 (createTime为null) ===");
            return;
        }

        System.out.println("=== ✅ createTime不为null，开始计算duration ===");
        System.out.println("=== 当前时间获取方式: 获取UTC时间并转换为用户本地时间 ===");

        // 获取UTC当前时间
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date utcCurrentTime = utcCalendar.getTime();
        System.out.println("=== UTC当前时间获取完成: " + utcCurrentTime + " ===");
        System.out.println("=== UTC当前时间(毫秒): " + utcCurrentTime.getTime() + " ===");

        // 将UTC当前时间转换为用户本地时间
        TimeZone userTZ = TimeZone.getTimeZone(userTimezone);
        Date endTime = convertUTCToUserLocalTime(utcCurrentTime, userTZ);
        System.out.println("=== UTC时间转换为用户本地时间: " + utcCurrentTime + " -> " + endTime + " ===");
        System.out.println("=== 用户本地当前时间(毫秒): " + endTime.getTime() + " ===");

        // 数据库中存储的时间已经是用户本地时间，直接使用
        Date adjustedCreateTime = issue.getCreateTime();
        System.out.println("=== 数据库创建时间处理策略: 数据库存储的是用户本地时间，直接使用 ===");

        System.out.println("=== 数据库中的createTime是用户本地时间，直接使用: " + adjustedCreateTime + " ===");
        System.out.println("=== 不需要进行时区转换 ===");

        System.out.println("=== ========== Duration计算公式详情 ========== ===");
        System.out.println("=== 计算公式: duration(小时) = (当前用户本地时间 - 创建用户本地时间) / (1000 * 60 * 60) ===");
        System.out.println("=== 公式说明: 除以1000转换毫秒->秒，除以60转换秒->分钟，除以60转换分钟->小时 ===");
        System.out.println("=== 当前用户本地时间: " + endTime + " ===");
        System.out.println("=== 当前用户本地时间(毫秒): " + endTime.getTime() + " ===");
        System.out.println("=== 数据库用户本地创建时间: " + adjustedCreateTime + " ===");
        System.out.println("=== 数据库用户本地创建时间(毫秒): " + adjustedCreateTime.getTime() + " ===");

        long diffInMillis = endTime.getTime() - adjustedCreateTime.getTime();
        System.out.println("=== 时间差计算: " + endTime.getTime() + " - " + adjustedCreateTime.getTime() + " = " + diffInMillis + " 毫秒 ===");

        // 如果时间差为负数，说明可能存在时区问题或数据异常
        if (diffInMillis < 0) {
            System.out.println("=== 警告：时间差为负数，可能存在时区问题或数据异常 ===");
            System.out.println("=== 将使用绝对值计算duration ===");
            diffInMillis = Math.abs(diffInMillis);
        }

        System.out.println("=== 是否为负数差值: " + (diffInMillis < 0 ? "是 (创建时间晚于当前时间)" : "否") + " ===");

        System.out.println("=== ========================================== ===");
        System.out.println("=== 开始逐步转换时间单位 ===");
        System.out.println("=== ========================================== ===");

        long durationInSeconds = diffInMillis / 1000;
        System.out.println("=== 步骤1 - 毫秒转秒: " + diffInMillis + " ÷ 1000 = " + durationInSeconds + " 秒 ===");

        long durationInMinutes = durationInSeconds / 60;
        System.out.println("=== 步骤2 - 秒转分钟: " + durationInSeconds + " ÷ 60 = " + durationInMinutes + " 分钟 ===");

        double durationInHoursDouble = durationInMinutes / 60.0;
        System.out.println("=== 步骤3 - 分钟转小时(double): " + durationInMinutes + " ÷ 60 = " + durationInHoursDouble + " 小时 ===");

        int durationInHours = (int) durationInHoursDouble;
        System.out.println("=== 步骤4 - 取整数部分(int): " + durationInHoursDouble + " -> " + durationInHours + " 小时 ===");

        System.out.println("=== ========================================== ===");
        System.out.println("=== Duration计算结果汇总: ===");
        System.out.println("=== ========================================== ===");
        System.out.println("=== 原始毫秒差值: " + diffInMillis + " 毫秒 ===");
        System.out.println("=== 转换为秒: " + durationInSeconds + " 秒 ===");
        System.out.println("=== 转换为分钟: " + durationInMinutes + " 分钟 ===");
        System.out.println("=== 转换为小时(精确): " + durationInHoursDouble + " 小时 ===");
        System.out.println("=== 最终Duration结果(整数): " + durationInHours + " 小时 ===");

        System.out.println("=== ========================================== ===");
        System.out.println("=== 设置Issue的duration字段 ===");
        System.out.println("=== ========================================== ===");
        System.out.println("=== 设置前Issue.duration: " + issue.getDuration() + " ===");
        issue.setDuration(durationInHours);
        System.out.println("=== 设置后Issue.duration: " + issue.getDuration() + " ===");

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
        System.out.println("=== ##########################################");
        System.out.println("=== IssueDurationCalculator.calculateDurationForList(issues, timezone) 批量方法开始");
        System.out.println("=== ##########################################");
        System.out.println("=== 批量计算参数信息: ===");
        System.out.println("=== - Issues列表: " + (issues != null ? "不为null" : "为null") + " ===");
        System.out.println("=== - Issues数量: " + (issues != null ? issues.size() : 0) + " ===");
        System.out.println("=== - 用户时区: " + userTimezone + " ===");

        if (issues == null || issues.isEmpty()) {
            System.out.println("=== ❌ Issue列表为空或null，跳过duration批量计算 ===");
            System.out.println("=== IssueDurationCalculator.calculateDurationForList() 批量方法结束 (列表为空) ===");
            return;
        }

        System.out.println("=== ✅ Issue列表不为空，开始批量计算 ===");
        System.out.println("=== 批量计算策略: 统一使用同一个当前时间点进行计算 ===");

        // 获取UTC当前时间，批量计算时统一使用同一个时间点
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date currentUtcTime = utcCalendar.getTime();
        System.out.println("=== 批量计算统一使用的当前时间: " + currentUtcTime + " ===");
        System.out.println("=== 批量计算统一使用的当前时间(毫秒): " + currentUtcTime.getTime() + " ===");

        System.out.println("=== ==========================================");
        System.out.println("=== 开始逐个处理Issue列表");
        System.out.println("=== ==========================================");

        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            System.out.println("=== 处理第 " + (i + 1) + "/" + issues.size() + " 个Issue ===");
            System.out.println("=== Issue ID: " + issue.getId() + " ===");

            if (issue.getCreateTime() != null) {
                System.out.println("=== ✅ Issue createTime不为null，调用单个计算方法 ===");
                calculateSingleIssueDuration(issue, currentUtcTime, userTimezone);
            } else {
                System.out.println("=== ❌ Issue ID " + issue.getId() + " createTime为null，设置duration=0 ===");
                issue.setDuration(0);
            }
            System.out.println("=== 第 " + (i + 1) + " 个Issue处理完成，当前duration: " + issue.getDuration() + " ===");
        }

        System.out.println("=== ##########################################");
        System.out.println("=== IssueDurationCalculator.calculateDurationForList() 批量方法完成");
        System.out.println("=== 总共处理了 " + issues.size() + " 个Issue");
        System.out.println("=== ##########################################");
    }

    /**
     * 计算单个Issue的duration（用于批量计算时复用逻辑）
     */
    private void calculateSingleIssueDuration(Issue issue, Date currentTime, String userTimezone) {
        System.out.println("=== ------------------------------------------");
        System.out.println("=== calculateSingleIssueDuration() 单个计算方法开始");
        System.out.println("=== ------------------------------------------");
        System.out.println("=== 输入参数:");
        System.out.println("=== - Issue ID: " + issue.getId() + " ===");
        System.out.println("=== - 当前时间: " + currentTime + " ===");
        System.out.println("=== - 当前时间(毫秒): " + currentTime.getTime() + " ===");
        System.out.println("=== - 用户时区: " + userTimezone + " ===");
        System.out.println("=== - Issue创建时间: " + issue.getCreateTime() + " ===");

        // 数据库中存储的时间已经是UTC时间，不需要再次转换
        Date adjustedCreateTime = issue.getCreateTime();
        System.out.println("=== 创建时间处理: 直接使用数据库时间，不进行时区转换 ===");
        System.out.println("=== - 调整后创建时间: " + adjustedCreateTime + " ===");
        System.out.println("=== - 调整后创建时间(毫秒): " + adjustedCreateTime.getTime() + " ===");

        System.out.println("=== 开始计算时间差:");
        long diffInMillis = currentTime.getTime() - adjustedCreateTime.getTime();
        System.out.println("=== - 毫秒差值: " + currentTime.getTime() + " - " + adjustedCreateTime.getTime() + " = " + diffInMillis + " ===");

        long durationInSeconds = diffInMillis / 1000;
        System.out.println("=== - 转换为秒: " + diffInMillis + " / 1000 = " + durationInSeconds + " ===");

        long durationInMinutes = durationInSeconds / 60;
        System.out.println("=== - 转换为分钟: " + durationInSeconds + " / 60 = " + durationInMinutes + " ===");

        double durationInHoursDouble = durationInMinutes / 60.0;
        System.out.println("=== - 转换为小时(double): " + durationInMinutes + " / 60 = " + durationInHoursDouble + " ===");

        int durationInHours = (int) durationInHoursDouble;
        System.out.println("=== - 转换为小时(int): " + durationInHoursDouble + " -> " + durationInHours + " ===");

        System.out.println("=== 设置Issue duration字段:");
        System.out.println("=== - 设置前: " + issue.getDuration() + " ===");
        issue.setDuration(durationInHours);
        System.out.println("=== - 设置后: " + issue.getDuration() + " ===");

        System.out.println("=== 单个计算结果汇总:");
        System.out.println("=== - Issue " + issue.getId() + " ===");
        System.out.println("=== - 从 " + adjustedCreateTime + " ===");
        System.out.println("=== - 到 " + currentTime + " ===");
        System.out.println("=== - 存活 " + durationInHours + " 小时 (" + durationInMinutes + " 分钟) ===");
        System.out.println("=== calculateSingleIssueDuration() 单个计算方法结束");
        System.out.println("=== ------------------------------------------");
    }

    /**
     * 将UTC时间转换为用户本地时间
     */
    private Date convertUTCToUserLocalTime(Date utcTime, TimeZone userTimeZone) {
        System.out.println("=== 开始UTC时间转换为用户本地时间 ===");
        System.out.println("=== 输入UTC时间: " + utcTime + " ===");
        System.out.println("=== 用户时区: " + userTimeZone.getID() + " ===");
        
        // 获取用户时区的偏移量（毫秒）
        long offsetMillis = userTimeZone.getOffset(utcTime.getTime());
        System.out.println("=== 时区偏移毫秒: " + offsetMillis + " ===");
        System.out.println("=== 时区偏移小时: " + (offsetMillis / (1000 * 60 * 60)) + " ===");
        
        // UTC时间 + 时区偏移 = 用户本地时间
        long localTimeMillis = utcTime.getTime() + offsetMillis;
        Date localTime = new Date(localTimeMillis);
        
        System.out.println("=== UTC时间毫秒: " + utcTime.getTime() + " ===");
        System.out.println("=== 本地时间毫秒: " + localTimeMillis + " ===");
        System.out.println("=== 转换后的用户本地时间: " + localTime + " ===");
        System.out.println("=== UTC时间转换为用户本地时间完成 ===");
        
        return localTime;
    }
}