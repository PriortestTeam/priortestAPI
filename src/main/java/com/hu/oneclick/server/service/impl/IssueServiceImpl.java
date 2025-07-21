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
    public void save(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== 新增Issue - 用户时区: " + userTimezone + " ===");

        // 转换日期到UTC
        convertDatesToUTC(issue, userTimezone);

        // 设置创建时间和修改时间
        Date now = new Date();
        issue.setCreateTime(now);
        issue.setUpdateTime(now);
        issue.setCreateUser(Long.parseLong(jwtUserService.getMasterId()));
        issue.setUpdateUser(Long.parseLong(jwtUserService.getMasterId()));

        this.save(issue);
    }

    @Override
    public void edit(IssueSaveDto dto) {
        Issue issue = this.getById(dto.getId());
        if (issue == null) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", dto.getId()));
        }

        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== 更新Issue - 用户时区: " + userTimezone + " ===");

        // 转换日期到UTC
        convertDatesToUTC(issue, userTimezone);

        // 设置修改时间
        issue.setUpdateTime(new Date());
        issue.setUpdateUser(Long.parseLong(jwtUserService.getMasterId()));

        this.updateById(issue);
    }

    @Override
    public PageInfo<Issue> list(IssueParam param) {
        List<Issue> issues = this.queryList(param);

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue列表查询 - 用户时区: " + userTimezone + " ===");

        // 为每个Issue计算duration和转换字段格式
        for (Issue issue : issues) {
            calculateDuration(issue, userTimezone);
            convertFieldsToString(issue);
        }

        return new PageInfo<>(issues);
    }

    @Override
    public void clone(List<Long> ids) {
        for (Long id : ids) {
            Issue originalIssue = this.getById(id);
            if (originalIssue != null) {
                Issue clonedIssue = new Issue();
                BeanUtil.copyProperties(originalIssue, clonedIssue);
                clonedIssue.setId(null); // 清除ID，让数据库自动生成新的
                clonedIssue.setTitle(originalIssue.getTitle() + " (克隆)");
                clonedIssue.setCreateTime(new Date());
                clonedIssue.setUpdateTime(new Date());
                clonedIssue.setCreateUser(Long.parseLong(jwtUserService.getMasterId()));
                clonedIssue.setUpdateUser(Long.parseLong(jwtUserService.getMasterId()));
                this.save(clonedIssue);
            }
        }
    }

    /**
     * 将用户本地时间转换为UTC时间存储到数据库
     */
    private void convertDatesToUTC(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 警告：没有提供用户时区信息，跳过时区转换 ===");
            return;
        }

        try {
            TimeZone userTz = TimeZone.getTimeZone(userTimezone);
            TimeZone utc = TimeZone.getTimeZone("UTC");

            // 转换planFixDate
            if (issue.getPlanFixDate() != null) {
                Date utcPlanFixDate = convertToUTC(issue.getPlanFixDate(), userTz, utc);
                issue.setPlanFixDate(utcPlanFixDate);
                System.out.println("=== 计划修复时间转换: 用户时区 " + issue.getPlanFixDate() + " -> UTC " + utcPlanFixDate + " ===");
            }

            // 转换closeDate
            if (issue.getCloseDate() != null) {
                Date utcCloseDate = convertToUTC(issue.getCloseDate(), userTz, utc);
                issue.setCloseDate(utcCloseDate);
                System.out.println("=== 关闭时间转换: 用户时区 " + issue.getCloseDate() + " -> UTC " + utcCloseDate + " ===");
            }

        } catch (Exception e) {
            System.err.println("=== 时区转换失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 将时间从用户时区转换为UTC
     */
    private Date convertToUTC(Date localDate, TimeZone fromTz, TimeZone toTz) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDate);
        calendar.setTimeZone(fromTz);

        calendar.setTimeZone(toTz);
        return calendar.getTime();
    }

    /**
     * 计算Issue存活时长
     */
    private void calculateDuration(Issue issue, String userTimezone) {
        if (issue.getCreateTime() == null) {
            issue.setDuration(0);
            return;
        }

        Date endTime;
        if (issue.getCloseDate() != null) {
            endTime = issue.getCloseDate();
        } else {
            endTime = new Date(); // 当前时间
        }

        // 计算时间差（单位：小时）
        long durationMillis = endTime.getTime() - issue.getCreateTime().getTime();
        int durationHours = (int) (durationMillis / (1000 * 60 * 60));

        // 检查时间差是否为负数
        if (durationHours < 0) {
            System.out.println("=== 警告：时间差为负数，可能存在时区问题或数据异常 ===");
            System.out.println("=== 创建时间: " + issue.getCreateTime() + ", 结束时间: " + endTime + " ===");
            durationHours = 0;
        }

        issue.setDuration(durationHours);
        System.out.println("=== Duration计算: " + durationHours + " 小时 (创建: " + issue.getCreateTime() + ", 结束: " + endTime + ") ===");
    }

    /**
     * 转换字段为字符串格式，确保前端能正确显示
     */
    private void convertFieldsToString(Issue issue) {
        // 这里可以添加其他需要格式化的字段
        if (issue.getIsLegacy() != null) {
            // 已经在Entity中处理
        }
        if (issue.getFoundAfterRelease() != null) {
            // 已经在Entity中处理
        }
    }

    /**
     * 查询Issue列表的具体实现
     */
    private List<Issue> queryList(IssueParam param) {
        QueryWrapper<Issue> queryWrapper = new QueryWrapper<>();

        // 添加查询条件
        if (param.getProjectId() != null) {
            queryWrapper.eq("project_id", param.getProjectId());
        }
        if (param.getTitle() != null && !param.getTitle().isEmpty()) {
            queryWrapper.like("title", param.getTitle());
        }
        if (param.getIssueStatus() != null && !param.getIssueStatus().isEmpty()) {
            queryWrapper.eq("issue_status", param.getIssueStatus());
        }

        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");

        return this.list(queryWrapper);
    }
}