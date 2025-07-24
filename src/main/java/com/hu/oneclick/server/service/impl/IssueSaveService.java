package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.common.util.TimezoneContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class IssueSaveService {

    @Resource
    private IssueDao issueDao;

    @Resource
    private IssueTimeConverter issueTimeConverter;

    @Resource
    private IssueDurationCalculator issueDurationCalculator;

    /**
     * 处理新增缺陷的所有逻辑
     */
    @Transactional
    public Issue saveNewIssue(IssueSaveDto dto) {
        // 记录服务器时区和时间信息
        logServerTimezoneAndTime("saveNewIssue");

        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        //String userTimezone = TimezoneContext.getUserTimezone();

        // 转换时间到UTC - 已注释，测试框架自动时区处理
        // issueTimeConverter.convertDatesToUTC(issue, userTimezone);

        // 处理自定义字段数据
        processCustomFields(issue, dto);

        // 处理版本相关字段
        processVersionFields(issue, dto);

        // 检查并转换日期型字段
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== 开始转换日期型字段 ===");
        System.out.println("=== 用户时区: " + userTimezone + " ===");

        if (issue.getPlanFixDate() != null) {
            Date originalPlanFixDate = issue.getPlanFixDate();
            System.out.println("=== planFixDate 字段转换前: " + originalPlanFixDate + " ===");
            System.out.println("=== planFixDate 转换前毫秒: " + originalPlanFixDate.getTime() + " ===");

            // 调用时间转换器将用户输入时间转换为UTC
            issueTimeConverter.convertUserInputTimeToUTC(issue, userTimezone);

            System.out.println("=== planFixDate 转换后: " + issue.getPlanFixDate() + " ===");
            System.out.println("=== planFixDate 转换后毫秒: " + (issue.getPlanFixDate() != null ? issue.getPlanFixDate().getTime() : "null") + " ===");
        } else {
            System.out.println("=== planFixDate 为空，跳过转换 ===");
        }

        // 打印即将插入数据库的时间信息
        System.out.println("=== 即将插入数据库的时间信息 ===");
        System.out.println("=== createTime: null ===");
        System.out.println("=== createTime (毫秒): null ===");
        System.out.println("=== updateTime: null ===");
        System.out.println("=== updateTime (毫秒): null ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");
        System.out.println("=== planFixDate (毫秒): " + (issue.getPlanFixDate() != null ? issue.getPlanFixDate().getTime() : "null") + " ===");

        issueDao.insert(issue);

        // 插入数据库后，再次打印确认
        System.out.println("=== 插入数据库后的时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");

        // 计算duration（基于UTC时间）
        System.out.println("=== saveNewIssue - 准备计算duration ===");
        issueDurationCalculator.calculateDuration(issue, userTimezone);
        System.out.println("=== saveNewIssue - duration计算完成，值: " + issue.getDuration() + " ===");

        System.out.println("=== updateExistingIssue完成，数据已更新到数据库（UTC时间） ===");
        System.out.println("=== 更新后的时间信息（数据库中的UTC时间） ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");
        System.out.println("=== 注意：时区转换将在上层方法中单独处理 ===");

        return issue;
    }

    /**
     * 处理编辑缺陷的所有逻辑
     */
    @Transactional
    public Issue updateExistingIssue(IssueSaveDto dto) {
        // 记录服务器时区和时间信息
        logServerTimezoneAndTime("updateExistingIssue");

        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        //String userTimezone = TimezoneContext.getUserTimezone();

        // 转换时间到UTC - 已注释，测试框架自动时区处理
        // issueTimeConverter.convertDatesToUTC(issue, userTimezone);

        // 处理自定义字段数据
        processCustomFields(issue, dto);

        // 处理版本相关字段
        processVersionFields(issue, dto);

           // 检查并转换日期型字段
            String userTimezone = TimezoneContext.getUserTimezone();
            System.out.println("=== 开始转换日期型字段 ===");
            System.out.println("=== 用户时区: " + userTimezone + " ===");

            if (issue.getPlanFixDate() != null) {
                Date originalPlanFixDate = issue.getPlanFixDate();
                System.out.println("=== planFixDate 字段转换前: " + originalPlanFixDate + " ===");
                System.out.println("=== planFixDate 转换前毫秒: " + originalPlanFixDate.getTime() + " ===");

                // 调用时间转换器将用户输入时间转换为UTC
                issueTimeConverter.convertUserInputTimeToUTC(issue, userTimezone);

                System.out.println("=== planFixDate 转换后: " + issue.getPlanFixDate() + " ===");
                System.out.println("=== planFixDate 转换后毫秒: " + (issue.getPlanFixDate() != null ? issue.getPlanFixDate().getTime() : "null") + " ===");
            } else {
                System.out.println("=== planFixDate 为空，跳过转换 ===");
            }
        

        // 在更新数据库之前，打印最终的时间值
        System.out.println("=== 即将更新数据库的时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== createTime (毫秒): " + (issue.getCreateTime() != null ? issue.getCreateTime().getTime() : "null") + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== updateTime (毫秒): " + (issue.getUpdateTime() != null ? issue.getUpdateTime().getTime() : "null") + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");
        System.out.println("=== planFixDate (毫秒): " + (issue.getPlanFixDate() != null ? issue.getPlanFixDate().getTime() : "null") + " ===");

        issueDao.updateById(issue);

        // 更新数据库后，再次打印确认
        System.out.println("=== 更新数据库后的时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");

        // 保持UTC时间返回给上层方法，不进行时区转换
        System.out.println("=== 数据库更新完成，返回UTC时间给上层方法处理 ===");
        System.out.println("=== 返回的UTC时间 - createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== 返回的UTC时间 - updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== 返回的UTC时间 - planFixDate: " + issue.getPlanFixDate() + " ===");

        return issue;
    }

    /**
     * 处理自定义字段数据
     */
    private void processCustomFields(Issue issue, IssueSaveDto dto) {
        Map<String, Object> customFieldMap = new HashMap<>();
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            customFieldMap = JSONUtil.toBean(JSONUtil.toJsonStr(dto.getCustomFieldDatas()), Map.class);
        }

        // 检查并添加新字段到自定义字段数据中
        if (dto.getIntroducedVersion() != null) {
            customFieldMap.put("introduced_version", dto.getIntroducedVersion());
        }
        if (dto.getIsLegacy() != null) {
            customFieldMap.put("is_legacy", dto.getIsLegacy());
        }
        if (dto.getFoundAfterRelease() != null) {
            customFieldMap.put("found_after_release", dto.getFoundAfterRelease());
        }

        // 保存自定义字段
        if (!customFieldMap.isEmpty()) {
            issue.setIssueExpand(JSONUtil.toJsonStr(customFieldMap));
        }
    }

    /**
     * 处理版本相关字段
     */
    private void processVersionFields(Issue issue, IssueSaveDto dto) {
        // 直接从 DTO 中获取三个版本相关字段
        if (dto.getIntroducedVersion() != null) {
            issue.setIntroducedVersion(dto.getIntroducedVersion());
        }
        if (dto.getIsLegacy() != null) {
            issue.setIsLegacy(dto.getIsLegacyAsInt());
        }
        if (dto.getFoundAfterRelease() != null) {
            issue.setFoundAfterRelease(dto.getFoundAfterReleaseAsInt());
        }
    }

    /**
     * 克隆缺陷
     */
    @Transactional
    public void cloneIssues(java.util.List<Long> ids) {
        // 记录服务器时区和时间信息
        logServerTimezoneAndTime("cloneIssues");

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();

        java.util.List<Issue> issueList = new java.util.ArrayList<>();
        java.util.Date currentTime = new java.util.Date();  // 当前时间作为克隆时间

        for (Long id : ids) {
            Issue issue = issueDao.selectById(id);
            if (issue == null) {
                throw new com.hu.oneclick.common.exception.BaseException(cn.hutool.core.util.StrUtil.format("缺陷查询不到。ID：{}", id));
            }
            Issue issueClone = new Issue();
            BeanUtil.copyProperties(issue, issueClone);

            // 重置关键字段
            issueClone.setId(null);  // 清空ID让数据库生成新ID
            issueClone.setTitle(com.hu.oneclick.common.util.CloneFormatUtil.getCloneTitle(issueClone.getTitle()));

            // 设置新的时间字段
            issueClone.setCreateTime(currentTime);      // 新的创建时间
            issueClone.setUpdateTime(currentTime);      // 新的更新时间
            issueClone.setPlanFixDate(currentTime);     // 计划修复时间设置为当前时间

            // 设置状态为新建
            issueClone.setIssueStatus("新建");

            // 转换时间到UTC - 已注释，测试框架自动时区处理
            // issueTimeConverter.convertDatesToUTC(issueClone, userTimezone);

            issueList.add(issueClone);
        }

        // 批量保存克隆的Issue
        for (Issue issue : issueList) {
            System.out.println("=== 克隆Issue插入前的时间信息 ===");
            System.out.println("=== Issue ID: " + issue.getId() + " ===");
            System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
            System.out.println("=== createTime (毫秒): " + (issue.getCreateTime() != null ? issue.getCreateTime().getTime() : "null") + " ===");
            System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
            System.out.println("=== updateTime (毫秒): " + (issue.getUpdateTime() != null ? issue.getUpdateTime().getTime() : "null") + " ===");
            System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");
            System.out.println("=== planFixDate (毫秒): " + (issue.getPlanFixDate() != null ? issue.getPlanFixDate().getTime() : "null") + " ===");

            issueDao.insert(issue);
        }
    }

    /**
     * 记录服务器时区和时间信息
     */
    private void logServerTimezoneAndTime(String methodName) {
        System.out.println("=== " + methodName + " - 服务器时区和时间信息 ===");

        // 获取默认时区
        TimeZone defaultTimeZone = TimeZone.getDefault();
        System.out.println("=== 服务器默认时区ID: " + defaultTimeZone.getID() + " ===");
        System.out.println("=== 服务器默认时区显示名: " + defaultTimeZone.getDisplayName() + " ===");

        // 获取系统时区
        ZoneId systemZoneId = ZoneId.systemDefault();
        System.out.println("=== 系统时区ID: " + systemZoneId.toString() + " ===");

        // 获取当前时间 - 多个时区
        Date currentDate = new Date();
        System.out.println("=== 服务器当前时间(Date): " + currentDate + " ===");

        // UTC时间
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneId.of("UTC"));
        System.out.println("=== UTC时间: " + utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")) + " ===");

        // 系统默认时区时间
        ZonedDateTime systemTime = ZonedDateTime.now(systemZoneId);
        System.out.println("=== 系统默认时区时间: " + systemTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")) + " ===");

        // 时区偏移信息
        int rawOffset = defaultTimeZone.getRawOffset();
        int offsetHours = rawOffset / (1000 * 60 * 60);
        System.out.println("=== 时区偏移: " + offsetHours + " 小时 (原始偏移: " + rawOffset + " 毫秒) ===");

        // 是否在夏令时
        boolean inDaylightTime = defaultTimeZone.inDaylightTime(currentDate);
        System.out.println("=== 是否夏令时: " + inDaylightTime + " ===");

        // 获取用户时区信息对比
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== 用户时区: " + (userTimezone != null ? userTimezone : "未设置") + " ===");

        if (userTimezone != null && !userTimezone.isEmpty()) {
            try {
                ZonedDateTime userTime = ZonedDateTime.now(ZoneId.of(userTimezone));
                System.out.println("=== 用户时区时间: " + userTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")) + " ===");
            } catch (Exception e) {
                System.out.println("=== 用户时区解析失败: " + e.getMessage() + " ===");
            }
        }

        System.out.println("=== " + methodName + " - 时区信息记录完成 ===");
    }
}