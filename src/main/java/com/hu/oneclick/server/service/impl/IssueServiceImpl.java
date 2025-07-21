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
import com.hu.oneclick.common.util.TimezoneContext;
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

/**
 * 缺陷(Issue)表服务实现类
 *
 * @author makejava
 * @since 2021-02-17 16:20:44
 */
@Service
public class IssueServiceImpl extends ServiceImpl<IssueDao, Issue> implements IssueService {

    private final static Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Resource
    private IssueDao issueDao;

    @Resource
    private ViewDao viewDao;

    @Resource
    private ModifyRecordsService modifyRecordsService;

    private final JwtUserServiceImpl jwtUserService;

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
    public List<Issue> list(IssueParam param) {
        return issueDao.selectList(null);
    }

    @Override
    public PageInfo<Issue> listWithViewFilter(IssueParam param, int pageNum, int pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Issue> issueList = issueDao.selectList(null);

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue列表查询 - 用户时区: " + userTimezone + " ===");

        // 为每个Issue计算duration并转换字段格式
        for (Issue issue : issueList) {
            calculateDuration(issue, userTimezone);
            convertFieldsToString(issue);
        }

        return new PageInfo<>(issueList);
    }

    @Override
    public Issue add(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue新增 - 用户时区: " + userTimezone + " ===");

        // 转换用户时区的时间为UTC时间存储到数据库
        convertToUtcForStorage(issue, userTimezone);

        issueDao.insert(issue);
        // 记录修改记录
        // modifyRecordsService.saveModifyRecords(issue, "新增");
        return issue;
    }

    @Override
    public Issue edit(IssueSaveDto dto) {
        if (dto.getId() == null) {
            throw new BaseException("ID不能为空");
        }

        Issue issue = issueDao.selectById(dto.getId());
        if (issue == null) {
            throw new BaseException("记录不存在");
        }

        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue编辑 - 用户时区: " + userTimezone + " ===");

        // 转换用户时区的时间为UTC时间存储到数据库
        convertToUtcForStorage(issue, userTimezone);

        issueDao.updateById(issue);
        // 记录修改记录
        // modifyRecordsService.saveModifyRecords(issue, "编辑");
        return issue;
    }

    @Override
    public Issue info(Long id) {
        Issue issue = issueDao.selectById(id);
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
            Issue originalIssue = issueDao.selectById(id);
            if (originalIssue != null) {
                Issue clonedIssue = CloneFormatUtil.cloneObject(originalIssue, Issue.class);
                clonedIssue.setId(null);
                clonedIssue.setTitle(originalIssue.getTitle() + "_copy");
                issueDao.insert(clonedIssue);
            }
        }
    }

    @Override
    public int studusedit(Issue issue, IssueStatusDto issueStatusDto) {
        BeanUtil.copyProperties(issueStatusDto, issue);
        int result = issueDao.updateById(issue);
        if (result > 0) {
            // 记录修改记录
            // modifyRecordsService.saveModifyRecords(issue, "状态修改");
        }
        return result;
    }

    @Override
    public Issue retrieveIssueStatusAsPerIssueId(Long projectId, Long issueId) {
        return issueDao.selectById(issueId);
    }

    @Override
    public PageInfo<Issue> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Issue> issueList = issueDao.selectList(null);

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue视图查询 - 用户时区: " + userTimezone + " ===");

        // 为每个Issue计算duration并转换字段格式
        for (Issue issue : issueList) {
            calculateDuration(issue, userTimezone);
            convertFieldsToString(issue);
        }

        return new PageInfo<>(issueList);
    }

    @Override
    public PageInfo<Issue> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Issue> issueList = issueDao.selectList(null);

        // 获取用户时区信息
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== Issue字段过滤查询 - 用户时区: " + userTimezone + " ===");

        // 为每个Issue计算duration并转换字段格式
        for (Issue issue : issueList) {
            calculateDuration(issue, userTimezone);
            convertFieldsToString(issue);
        }

        return new PageInfo<>(issueList);
    }

    /**
     * 将用户时区的时间转换为UTC时间存储到数据库
     */
    private void convertToUtcForStorage(Issue issue, String userTimezone) {
        try {
            TimeZone userTz = TimeZone.getTimeZone(userTimezone);
            TimeZone utcTz = TimeZone.getTimeZone("UTC");

            System.out.println("=== 时区转换（存储前）===");

            if (issue.getPlanFixDate() != null) {
                Date originalPlanFixDate = issue.getPlanFixDate();
                Date utcPlanFixDate = convertTimeToUtc(originalPlanFixDate, userTz, utcTz);
                issue.setPlanFixDate(utcPlanFixDate);
                System.out.println("计划修复时间: " + originalPlanFixDate + " -> " + utcPlanFixDate);
            }

            if (issue.getCloseDate() != null) {
                Date originalCloseDate = issue.getCloseDate();
                Date utcCloseDate = convertTimeToUtc(originalCloseDate, userTz, utcTz);
                issue.setCloseDate(utcCloseDate);
                System.out.println("关闭时间: " + originalCloseDate + " -> " + utcCloseDate);
            }

            System.out.println("=== 时区转换完成 ===");
        } catch (Exception e) {
            System.err.println("时区转换出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将时间从用户时区转换为UTC时区
     */
    private Date convertTimeToUtc(Date localTime, TimeZone fromTimeZone, TimeZone toTimeZone) {
        long localTimeInMillis = localTime.getTime();
        int fromOffset = fromTimeZone.getOffset(localTimeInMillis);
        int toOffset = toTimeZone.getOffset(localTimeInMillis);
        return new Date(localTimeInMillis - fromOffset + toOffset);
    }

    /**
     * 计算存活时长（基于UTC时间）
     */
    private void calculateDuration(Issue issue, String userTimezone) {
        try {
            Date createTime = issue.getCreateTime();
            Date closeDate = issue.getCloseDate();

            System.out.println("=== Duration计算 ===");
            System.out.println("创建时间(UTC): " + createTime);
            System.out.println("关闭时间(UTC): " + closeDate);

            if (createTime != null) {
                Date endTime = closeDate != null ? closeDate : new Date();
                long durationMs = endTime.getTime() - createTime.getTime();
                int durationHours = (int) (durationMs / (1000 * 60 * 60));
                issue.setDuration(Math.max(0, durationHours));

                System.out.println("结束时间(UTC): " + endTime);
                System.out.println("存活时长: " + issue.getDuration() + " 小时");
            } else {
                issue.setDuration(0);
                System.out.println("创建时间为空，duration设为0");
            }
            System.out.println("=== Duration计算完成 ===");
        } catch (Exception e) {
            System.err.println("Duration计算出错: " + e.getMessage());
            e.printStackTrace();
            issue.setDuration(0);
        }
    }

    /**
     * 转换字段格式，确保返回给前端的是正确的格式
     */
    private void convertFieldsToString(Issue issue) {
        // 这个方法主要用于确保数据格式正确
        // 目前Issue实体已经有相应的getter方法处理格式转换
        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }
    }
}