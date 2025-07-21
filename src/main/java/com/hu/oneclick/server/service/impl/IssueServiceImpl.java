
package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.dto.IssueStatusDto;
import com.hu.oneclick.model.param.IssueParam;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.IssueService;
import com.hu.oneclick.server.service.ModifyRecordsService;
import com.hu.oneclick.server.service.QueryFilterService;
import com.hu.oneclick.server.service.ViewFilterService;
import com.hu.oneclick.config.TimezoneContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.zhxu.bs.MapSearcher;

@Service
public class IssueServiceImpl extends ServiceImpl<IssueDao, Issue> implements IssueService {

    private final static Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Resource
    private IssueDao issueDao;

    @Resource
    private ViewDao viewDao;

    @Resource
    private MapSearcher mapSearcher;

    private final JwtUserServiceImpl jwtUserService;

    private final ModifyRecordsService modifyRecordsService;

    private final SysPermissionService sysPermissionService;

    private final QueryFilterService queryFilterService;

    private final CustomFieldDataService customFieldDataService;

    private final ViewFilterService viewFilterService;

    public IssueServiceImpl(JwtUserServiceImpl jwtUserService,
                           ModifyRecordsService modifyRecordsService,
                           SysPermissionService sysPermissionService,
                           QueryFilterService queryFilterService,
                           CustomFieldDataService customFieldDataService,
                           ViewFilterService viewFilterService) {
        this.jwtUserService = jwtUserService;
        this.modifyRecordsService = modifyRecordsService;
        this.sysPermissionService = sysPermissionService;
        this.queryFilterService = queryFilterService;
        this.customFieldDataService = customFieldDataService;
        this.viewFilterService = viewFilterService;
    }

    @Override
    public PageInfo<Issue> list(IssueParam param) {
        // 实现列表查询逻辑
        PageUtil.initPage(param);
        
        Issue issue = new Issue();
        BeanUtil.copyProperties(param, issue);
        
        List<Issue> issues = issueDao.queryList(issue);
        
        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue列表查询 - 用户时区: " + userTimezone + " ===");
        
        // 为每个issue计算duration并转换字段格式
        for (Issue issueItem : issues) {
            calculateDuration(issueItem, userTimezone);
            convertFieldsToString(issueItem);
        }
        
        return new PageInfo<>(issues);
    }

    @Override
    public Issue save(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue保存 - 用户时区: " + userTimezone + " ===");
        
        // 转换日期到UTC
        convertDatesToUTC(issue, userTimezone);

        // 保存到数据库
        this.save(issue);
        
        // 记录修改历史
        modifyRecordsService.saveModifyRecords(issue, "新增");
        
        return issue;
    }

    @Override
    public Issue edit(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue编辑 - 用户时区: " + userTimezone + " ===");

        // 转换日期到UTC
        convertDatesToUTC(issue, userTimezone);

        // 更新到数据库
        this.updateById(issue);
        
        // 记录修改历史
        modifyRecordsService.saveModifyRecords(issue, "编辑");
        
        return issue;
    }

    @Override
    public Issue info(Long id) {
        Issue issue = this.getById(id);
        if (issue == null) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", id));
        }

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue详情查询 - 用户时区: " + userTimezone + " ===");

        // 计算duration（基于UTC时间）
        calculateDuration(issue, userTimezone);

        // 转换字段格式，确保返回给前端的是字符串格式
        convertFieldsToString(issue);

        return issue;
    }

    @Override
    public void clone(List<Long> ids) {
        for (Long id : ids) {
            Issue originalIssue = this.getById(id);
            if (originalIssue != null) {
                Issue clonedIssue = CloneFormatUtil.clone(originalIssue);
                clonedIssue.setId(null); // 重置ID以便创建新记录
                clonedIssue.setTitle(originalIssue.getTitle() + " (副本)");
                this.save(clonedIssue);
            }
        }
    }

    @Override
    public void updateStatus(IssueStatusDto dto) {
        Issue issue = this.getById(dto.getId());
        if (issue != null) {
            issue.setIssueStatus(dto.getIssueStatus());
            issue.setVerifiedResult(dto.getVerifiedResult());
            this.updateById(issue);
            
            // 记录修改历史
            modifyRecordsService.saveModifyRecords(issue, "状态更新");
        }
    }

    /**
     * 将用户本地时间转换为UTC时间存储
     */
    private void convertDatesToUTC(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 警告：时区信息为空，跳过时区转换 ===");
            return;
        }

        try {
            TimeZone userTz = TimeZone.getTimeZone(userTimezone);
            TimeZone utcTz = TimeZone.getTimeZone("UTC");

            // 转换计划修复时间
            if (issue.getPlanFixDate() != null) {
                Date utcDate = convertToUTC(issue.getPlanFixDate(), userTz, utcTz);
                issue.setPlanFixDate(utcDate);
                System.out.println("=== 计划修复时间已转换为UTC ===");
            }

            // 转换关闭时间
            if (issue.getCloseDate() != null) {
                Date utcDate = convertToUTC(issue.getCloseDate(), userTz, utcTz);
                issue.setCloseDate(utcDate);
                System.out.println("=== 关闭时间已转换为UTC ===");
            }

        } catch (Exception e) {
            System.err.println("=== 时区转换失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 将日期从用户时区转换为UTC
     */
    private Date convertToUTC(Date localDate, TimeZone fromTimeZone, TimeZone toTimeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDate);
        calendar.setTimeZone(fromTimeZone);
        
        long timeInMillis = calendar.getTimeInMillis();
        calendar.setTimeZone(toTimeZone);
        calendar.setTimeInMillis(timeInMillis);
        
        return calendar.getTime();
    }

    /**
     * 计算Issue存活时长
     */
    private void calculateDuration(Issue issue, String userTimezone) {
        try {
            Date createTime = issue.getCreateTime();
            Date closeTime = issue.getCloseDate();
            Date currentTime = closeTime != null ? closeTime : new Date();

            if (createTime != null) {
                long diffInMillis = currentTime.getTime() - createTime.getTime();
                long diffInHours = diffInMillis / (1000 * 60 * 60);
                
                if (diffInHours < 0) {
                    System.out.println("=== 警告：时间差为负数，可能存在时区问题或数据异常 ===");
                    issue.setDuration(0);
                } else {
                    issue.setDuration((int) diffInHours);
                }
                
                System.out.println("=== Duration计算完成: " + issue.getDuration() + " 小时 ===");
            } else {
                issue.setDuration(0);
            }
        } catch (Exception e) {
            System.err.println("=== Duration计算失败: " + e.getMessage() + " ===");
            issue.setDuration(0);
        }
    }

    /**
     * 转换字段为字符串格式，确保前端能正确显示
     */
    private void convertFieldsToString(Issue issue) {
        // 这里可以添加任何需要特殊格式化的字段转换逻辑
        // 例如将某些数字字段转换为字符串等
    }
}
