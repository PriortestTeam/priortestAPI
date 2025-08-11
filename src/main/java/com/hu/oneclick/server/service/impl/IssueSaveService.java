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
        return insertNewIssue(dto);
    }

    /**
     * 处理新增缺陷的所有逻辑
     */
    @Transactional
    public Issue insertNewIssue(IssueSaveDto dto) {
        // 记录服务器时区和时间信息
        //logServerTimezoneAndTime("insertNewIssue");

        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 处理自定义字段数据
        processCustomFields(issue, dto);

        // 处理版本相关字段
        processVersionFields(issue, dto);

        // 获取用户时区并转换日期型字段 - 已注释
        String userTimezone = TimezoneContext.getUserTimezone();
        // System.out.println("=== insertNewIssue - 开始转换日期型字段 ===");
        // System.out.println("=== 用户时区: " + userTimezone + " ===");

        // // 转换主要日期字段到UTC - 已注释
        // if (issue.getPlanFixDate() != null) {
        //     Date originalPlanFixDate = issue.getPlanFixDate();
        //     System.out.println("=== planFixDate 字段转换前: " + originalPlanFixDate + " ===");
        //     issueTimeConverter.convertMainTimeFieldsToUTC(issue, userTimezone);
        //     System.out.println("=== planFixDate 转换后: " + issue.getPlanFixDate() + " ===");
        // }

        // // 转换自定义字段中的日期到UTC（只处理attributes，不重复转换主要字段） - 已注释
        // if (issue.getIssueExpand() != null && !issue.getIssueExpand().isEmpty()) {
        //     System.out.println("=== 开始处理issueExpand中的日期字段 ===");
        //     issueTimeConverter.convertCustomFieldsTimeToUTC(issue, userTimezone);
        // }

        // 确保字段不为null
        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }

        System.out.println("=== 执行新增操作 ===");
        issueDao.insert(issue);

        System.out.println("=== 插入数据库后的时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");

        // 计算duration（基于UTC时间） - 已注释
        // System.out.println("=== insertNewIssue - 准备计算duration ===");
        // issueDurationCalculator.calculateDuration(issue, userTimezone);
        // System.out.println("=== insertNewIssue - duration计算完成，值: " + issue.getDuration() + " ===");

        // 转换UTC时间回用户本地时区（用于返回给前端显示） - 已注释
        // System.out.println("=== 开始将UTC时间转换为用户本地时区 ===");
        // issueTimeConverter.convertUTCToLocalTime(issue, userTimezone);
        // System.out.println("=== UTC到本地时区转换完成 ===");
        // System.out.println("=== 返回给用户的时间信息 ===");
        // System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        // System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        // System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");

        // System.out.println("=== insertNewIssue完成，数据已插入到数据库（UTC时间），返回用户本地时区时间 ===");
        return issue;
    }

    /**
     * 处理更新缺陷的所有逻辑
     */
    @Transactional
    public Issue updateExistingIssue(IssueSaveDto dto) {
        // 记录服务器时区和时间信息
        logServerTimezoneAndTime("updateExistingIssue");

        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 处理自定义字段数据
        processCustomFields(issue, dto);

        // 处理版本相关字段
        processVersionFields(issue, dto);

        // 确保duration字段被正确复制
        if (dto.getDuration() != null) {
            issue.setDuration(dto.getDuration());
            System.out.println("=== updateExistingIssue: duration字段已设置: " + issue.getDuration() + " ===");
        }

        // 获取用户时区并转换日期型字段
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== updateExistingIssue - 开始转换日期型字段 ===");
        System.out.println("=== 用户时区: " + userTimezone + " ===");


        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }

        issueDao.updateById(issue);

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

    // 4. 转换用户输入的本地时间为UTC时间（用于存储到数据库） - 已注释
    public void convertUserInputTimeToUTC(Issue issue, String userTimezone) {

    }
}