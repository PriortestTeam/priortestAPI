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
       
        System.out.println("=== IssueDurationCalculator.calculateDuration(issue, timezone) 方法开始 ===");
        System.out.println("=== ========================================== ===");
        System.out.println("=== 输入参数信息: ===");
        System.out.println("=== Issue ID: " + issue.getId() + " ===");
        System.out.println("=== Issue对象: " + issue + " ===");
        System.out.println("=== 用户时区参数: " + userTimezone + " ===");
        System.out.println("=== Issue createTime原始值: " + issue.getCreateTime() + " ===");
        System.out.println("=== Issue runcaseId: " + issue.getRuncaseId() + " (runcaseId为null是正常的) ===");

        if (issue.getCreateTime() == null) {
            System.out.println("=== ❌ createTime为null，无法计算duration，设置为0 ===");
            issue.setDuration(0);
            System.out.println("=== IssueDurationCalculator.calculateDuration() 方法结束 (createTime为null) ===");
            return;
        }

        System.out.println("=== ✅ createTime不为null，开始计算duration ===");
        System.out.println("=== 当前时间获取方式: 直接使用当前时间，不进行时区转换 ===");

        // 直接获取当前时间，不进行时区转换
        Date currentTime = new Date();
        System.out.println("=== 当前时间获取完成: " + currentTime + " ===");
        System.out.println("=== 当前时间(毫秒): " + currentTime.getTime() + " ===");
         System.out.println("=== ========================================== ===");
        
        // 直接使用数据库中的创建时间，不进行任何时区转换
        Date createTime = issue.getCreateTime();
        System.out.println("=== 数据库创建时间处理策略: 直接使用原始时间，不进行时区转换 ===");
        System.out.println("=== 数据库原始createTime: " + createTime + " ===");
        System.out.println("=== 数据库原始createTime(毫秒): " + createTime.getTime() + " ===");

        System.out.println("=== ========== Duration计算公式详情 ========== ===");
        System.out.println("=== 计算公式: duration(小时) = (当前时间 - 创建时间) / (1000 * 60 * 60) ===");
      
        System.out.println("=== 当前时间: " + currentTime + " ===");
        System.out.println("=== 数据库创建时间: " + createTime + " ===");
       
        long diffInMillis = currentTime.getTime() - createTime.getTime();
        System.out.println("=== 时间差计算: " + currentTime.getTime() + " - " + createTime.getTime() + " = " + diffInMillis + " 毫秒 ===");

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
        long durationInMinutes = durationInSeconds / 60;
        double durationInHoursDouble = durationInMinutes / 60.0;
        System.out.println("=== 步骤 - 分钟转小时(double): " + durationInMinutes + " ÷ 60 = " + durationInHoursDouble + " 小时 ===");

        int durationInHours = (int) durationInHoursDouble;
        System.out.println("=== 步骤 - 取整数部分(int): " + durationInHoursDouble + " -> " + durationInHours + " 小时 ===");

        System.out.println("=== ========================================== ===");
        System.out.println("=== 最终Duration结果(整数): " + durationInHours + " 小时 ===");

        System.out.println("=== ========================================== ===");
        System.out.println("=== 设置前Issue.duration: " + issue.getDuration() + " ===");
        issue.setDuration(durationInHours);
        System.out.println("=== 设置后Issue.duration: " + issue.getDuration() + " ===");

        System.out.println("=== ========== Duration时间信息汇总 ========== ===");
        System.out.println("=== Issue ID: " + issue.getId() + " ===");
        System.out.println("=== 创建时间: " + createTime + " ===");
        System.out.println("=== 当前时间: " + currentTime + " ===");
        System.out.println("=== 存活时长: " + durationInHours + " 小时 ===");
       
        System.out.println("=== IssueDurationCalculator - Duration计算完成 ===");
        System.out.println("=== ========================================== ===");
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
      
        System.out.println("=== - 用户时区: " + userTimezone + " ===");
        System.out.println("=== - Issue创建时间: " + issue.getCreateTime() + " ===");
        System.out.println("=== - Issue runcaseId: " + issue.getRuncaseId() + " (可能为null，这是正常的) ===");

        // 数据库中存储的时间已经是UTC时间，不需要再次转换
        Date adjustedCreateTime = issue.getCreateTime();
        System.out.println("=== 创建时间处理: 直接使用数据库时间，不进行时区转换 ===");
        System.out.println("=== - 调整后创建时间: " + adjustedCreateTime + " ===");
        System.out.println("=== - 调整后创建时间(毫秒): " + adjustedCreateTime.getTime() + " ===");

        System.out.println("=== 开始计算时间差:");
        long diffInMillis = currentTime.getTime() - adjustedCreateTime.getTime();
    
        long durationInSeconds = diffInMillis / 1000;
     

        long durationInMinutes = durationInSeconds / 60;
      

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
        System.out.println("=== 🔍🔍 convertUTCToUserLocalTime方法开始 ===");
       
        System.out.println("=== 🔍🔍 utcTime: " + utcTime + " ===");
        System.out.println("=== 🔍🔍 userTimeZone: " + userTimeZone + " ===");
        
        if (utcTime == null) {
            System.out.println("=== 🔍🔍 ❌ utcTime为null ===");
            return null;
        }
        
        if (userTimeZone == null) {
            System.out.println("=== 🔍🔍 ❌ userTimeZone为null ===");
            return utcTime; // 如果时区为null，直接返回原时间
        }
        
        System.out.println("=== 🔍🔍 userTimeZone.getID(): " + userTimeZone.getID() + " ===");
        System.out.println("=== 开始UTC时间转换为用户本地时间 ===");
        System.out.println("=== 输入UTC时间: " + utcTime + " ===");
        System.out.println("=== 用户时区: " + userTimeZone.getID() + " ===");
        
        // 获取服务器当前时区
        TimeZone serverTimeZone = TimeZone.getDefault();
        System.out.println("=== 服务器时区: " + serverTimeZone.getID() + " ===");
        
        // 获取用户时区和服务器时区的偏移量
        long userOffsetMillis = userTimeZone.getOffset(utcTime.getTime());
        long serverOffsetMillis = serverTimeZone.getOffset(utcTime.getTime());
      
        System.out.println("=== 用户时区偏移小时: " + (userOffsetMillis / (1000 * 60 * 60)) + " ===");
        System.out.println("=== 服务器时区偏移小时: " + (serverOffsetMillis / (1000 * 60 * 60)) + " ===");
        
        // 检查时区是否相同
        if (userOffsetMillis == serverOffsetMillis) {
            System.out.println("=== 用户时区与服务器时区相同，直接返回UTC时间（服务器会自动显示为本地时间） ===");
            System.out.println("=== 输入UTC时间: " + utcTime + " ===");
            System.out.println("=== 返回时间: " + utcTime + " ===");
            return utcTime;
        } else {
            // 时区不同时才进行转换
            System.out.println("=== 用户时区与服务器时区不同，需要进行时区转换 ===");
            long offsetDiff = userOffsetMillis - serverOffsetMillis;
            long localTimeMillis = utcTime.getTime() + offsetDiff;
            Date localTime = new Date(localTimeMillis);
            
            System.out.println("=== 时区差异毫秒: " + offsetDiff + " ===");
         
            System.out.println("=== 转换后的用户本地时间: " + localTime + " ===");
            return localTime;
        }
    }
}