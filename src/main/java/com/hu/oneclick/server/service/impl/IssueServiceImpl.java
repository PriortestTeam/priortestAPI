package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import com.hu.oneclick.common.util.TimezoneContext;
import com.hu.oneclick.model.base.Resp;

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

    @Resource
    private IssueSaveService issueSaveService;

    @Resource
    private IssueTimeConverter issueTimeConverter;

    @Resource
    private IssueDurationCalculator issueDurationCalculator;

    // ThreadLocal存储用户时区信息
    private static final ThreadLocal<String> USER_TIMEZONE = new ThreadLocal<>();

    public static void setUserTimezone(String timezone) {
        USER_TIMEZONE.set(timezone);
    }

    public static String getCurrentUserTimezone() {
        return USER_TIMEZONE.get();
    }

    public static void clearUserTimezone() {
        USER_TIMEZONE.remove();
    }


    public IssueServiceImpl(JwtUserServiceImpl jwtUserService, ModifyRecordsService modifyRecordsService, SysPermissionService sysPermissionService, QueryFilterService queryFilterService, CustomFieldDataService customFieldDataService, ViewFilterService viewFilterService) {
        this.jwtUserService = jwtUserService;
        this.modifyRecordsService = modifyRecordsService;
        this.sysPermissionService = sysPermissionService;
        this.queryFilterService = queryFilterService;
        this.customFieldDataService = customFieldDataService;
        this.viewFilterService = viewFilterService;
    }



    @Override
    public List<Issue> list(IssueParam param) {
        return this.list(param.getQueryCondition());
    }

    @Override
    @Transactional
    public Issue add(IssueSaveDto dto) {
        return issueSaveService.saveNewIssue(dto);
    }

    @Override
    @Transactional
    public Issue edit(IssueSaveDto dto) {
        Issue entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }

        // 1. 更新数据库记录（只做UTC转换和存储，不做返回转换）
        Issue issue = issueSaveService.updateExistingIssue(dto);

        // 2. 重新查询完整的Issue对象，确保包含所有字段（从数据库查询的都是UTC时间）
        Issue completeIssue = this.getByIdAndProjectId(issue.getId(), issue.getProjectId());
        if (completeIssue != null) {
            // 将更新后的字段值复制到完整对象中，但时间字段使用数据库查询的UTC时间
            completeIssue.setTitle(issue.getTitle());
            // 注意：planFixDate使用数据库查询的UTC时间，不使用updateExistingIssue返回的已转换时间
            completeIssue.setVerifiedResult(issue.getVerifiedResult());
            completeIssue.setPriority(issue.getPriority());
            completeIssue.setEnv(issue.getEnv());
            completeIssue.setBrowser(issue.getBrowser());
            completeIssue.setPlatform(issue.getPlatform());
            completeIssue.setIssueVersion(issue.getIssueVersion());
            completeIssue.setCaseCategory(issue.getCaseCategory());
            completeIssue.setDescription(issue.getDescription());
            completeIssue.setIssueStatus(issue.getIssueStatus());
            completeIssue.setModule(issue.getModule());
            completeIssue.setReportTo(issue.getReportTo());
            completeIssue.setIssueExpand(issue.getIssueExpand());
            completeIssue.setFixVersion(issue.getFixVersion());
            completeIssue.setIntroducedVersion(issue.getIntroducedVersion());
            completeIssue.setIsLegacy(issue.getIsLegacy());
            completeIssue.setFoundAfterRelease(issue.getFoundAfterRelease());
            completeIssue.setSeverity(issue.getSeverity());
            completeIssue.setTestDevice(issue.getTestDevice());
            completeIssue.setRuncaseId(issue.getRuncaseId());
            completeIssue.setFixCategory(issue.getFixCategory());
            completeIssue.setFrequency(issue.getFrequency());
            completeIssue.setIssueSource(issue.getIssueSource());
            completeIssue.setUserImpact(issue.getUserImpact());
            completeIssue.setRootCause(issue.getRootCause());
            completeIssue.setRootcauseCategory(issue.getRootcauseCategory());
            // 注意：updateTime也使用数据库查询的UTC时间，不使用updateExistingIssue返回的已转换时间
            completeIssue.setUpdateUserId(issue.getUpdateUserId());

            issue = completeIssue;
        }

        // 3. 计算duration和处理其他字段（基于UTC时间）
        convertFieldsToStringForEdit(issue);

        // 4. 最后进行时区转换，将UTC时间转换为用户本地时间用于返回前端（只转换一次）
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== edit方法最后阶段 - 开始UTC到本地时间转换 ===");
        System.out.println("=== 用户时区: " + userTimezone + " ===");
        issueTimeConverter.convertUTCToLocalTime(issue, userTimezone);
        System.out.println("=== edit方法 - 时区转换完成，返回给前端 ===");

        return issue;
    }


    /**
     * 专门用于编辑操作的字段转换方法，只处理业务逻辑，不进行时区转换
     * 时区转换在上层方法中单独处理
     */
    private void convertFieldsToStringForEdit(Issue issue) {
        System.out.println("=== convertFieldsToStringForEdit开始 - Issue ID: " + issue.getId() + " ===");
        System.out.println("=== Duration进入convertFieldsToStringForEdit前: " + issue.getDuration() + " ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== convertFieldsToStringForEdit中获取的用户时区: " + userTimezone + " ===");

        // 计算duration（基于UTC时间，不进行时区转换）
        if (issue.getDuration() == null) {
            System.out.println("=== 开始计算duration（基于UTC时间） ===");
            issueDurationCalculator.calculateDuration(issue, userTimezone);
            System.out.println("=== duration计算完成，值: " + issue.getDuration() + " ===");
        }

        // 确保 isLegacy 和 foundAfterRelease 不为null
        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }

        System.out.println("=== convertFieldsToStringForEdit结束 - Issue ID: " + issue.getId() + ", Duration最终: " + issue.getDuration() + " ===");
        System.out.println("=== 注意：时区转换将在上层方法中单独处理 ===");
    }

    /**
     * 专门为 issue/list API 设计的字段转换方法
     * 直接进行时区转换，不做任何检查
     */
    private void convertFieldsToStringForList(Issue issue) {
        System.out.println("=== convertFieldsToStringForList开始 - Issue ID: " + issue.getId() + " ===");
        
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== 用户时区: " + userTimezone + " ===");

        // 1. 计算duration（基于UTC时间）
        issueDurationCalculator.calculateDuration(issue, userTimezone);
        System.out.println("=== duration计算完成: " + issue.getDuration() + " ===");

        // 2. 转换UTC时间为用户本地时间
        issueTimeConverter.convertUTCToLocalTime(issue, userTimezone);
        System.out.println("=== 时区转换完成 ===");

        // 3. 确保字段不为null
        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }

        System.out.println("=== convertFieldsToStringForList结束 ===");
    }

    private Issue getByIdAndProjectId(Long id, Long projectId) {
        QueryWrapper<Issue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Issue::getId, id)
                .eq(Issue::getProjectId, projectId);
        Issue issue = this.baseMapper.selectOne(queryWrapper);
        return issue;
    }

    @Override
    public Issue info(Long id) {
        Issue issue = this.baseMapper.selectById(id);
        if (issue == null) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", id));
        }

        System.out.println("=== info方法开始处理Issue ID: " + id + " ===");
        
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== 用户时区: " + userTimezone + " ===");

        // 计算duration（基于UTC时间）
        issueDurationCalculator.calculateDuration(issue, userTimezone);
        System.out.println("=== duration计算完成: " + issue.getDuration() + " ===");

        // 转换UTC时间为用户本地时间
        issueTimeConverter.convertUTCToLocalTime(issue, userTimezone);
        System.out.println("=== 时区转换完成 ===");

        // 确保字段不为null
        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }

        System.out.println("=== info方法处理完成 ===");
        return issue;
    }

    @Override
    public int studusedit(Issue issue,IssueStatusDto issueStatusDto) {
       issue.setFixVersion(issueStatusDto.getFixVersion());
       issue.setIssueStatus(issueStatusDto.getIssueStatus());
       issue.setVerifiedResult(issueStatusDto.getVerifiedResult());
        // 如果 status 是 关闭 时，设置 close_date 时间为此时
        if ("关闭".equals(issueStatusDto.getIssueStatus())) {
           // issue.setCloseDate(new Date()); // Removed closeDate
        }
       System.out.println(issue);

        return baseMapper.updateById(issue);
    }

    @Override
    public void clone(List<Long> ids) {
        issueSaveService.cloneIssues(ids);
    }

    @Override
    public Issue retrieveIssueStatusAsPerIssueId(Long projectId, Long issueId) {
        return this.getByIdAndProjectId(issueId, projectId);
    }

    @Override
    public PageInfo<Issue> listWithViewFilter(IssueParam param, int pageNum, int pageSize) {
        // 检查是否需要应用视图过滤
        if (viewFilterService.shouldApplyViewFilter(param.getViewId())) {
            // 使用视图过滤进行查询
            return listWithViewFilterLogic(param, pageNum, pageSize);
        } else {
            // 使用原有的简单查询逻辑
            return list(param, pageNum, pageSize);
        }
    }

    @Override
    public PageInfo<Issue> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize) {
        try {
            // 获取视图过滤参数
            Map<String, Object> filterParams = viewFilterService.getFilterParamsByViewId(viewId, projectId);

            if (filterParams == null) {
                // 如果没有过滤条件，返回空分页结果
                return new PageInfo<>(new ArrayList<>());
            }

            // 使用BeanSearcher进行查询，使用issue作为查询类
            Class<?> issueClass = Class.forName("com.hu.oneclick.model.entity.Issue");

            // 使用与 BeanSearchController 完全相同的逻辑：searchAll + manualPaging
            List<Map<String, Object>> result = mapSearcher.searchAll(issueClass, filterParams);

            // 转换为 Issue 对象
            List<Issue> issueList = result.stream()
                .map(map -> BeanUtil.toBeanIgnoreError(map, Issue.class))
                .collect(Collectors.toList());

            // 转换字段格式，确保返回给前端的是字符串格式
            issueList.forEach(this::convertFieldsToStringForList);

            // 使用与 BeanSearchController 相同的分页处理方式
            return PageUtil.manualPaging(issueList);
        } catch (Exception e) {
            logger.error("使用BeanSearcher查询缺陷失败，viewId: {}, projectId: {}", viewId, projectId, e);
            return new PageInfo<>(new ArrayList<>());
        }
    }

    @Override
    public PageInfo<Issue> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize) {
        // 1. 确定表名
        String tableName = null;
        switch (scopeName) {
            case "故事": tableName = "feature"; break;
            case "测试用例": tableName = "test_case"; break;
            case "缺陷": tableName = "issue"; break;
            case "测试周期": tableName = "test_cycle"; break;
            default: tableName = "issue";
        }
        // 2. 获取 projectId
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();

        // 3. 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 添加调试日志
        logger.info("queryByFieldAndValue - 分页参数: pageNum={}, pageSize={}, offset={}", pageNum, pageSize, offset);
        logger.info("queryByFieldAndValue - 查询参数: tableName={}, fieldNameEn={}, value={}, projectId={}", tableName, fieldNameEn, value, projectId);

        // 4. 使用 DAO 方法查询数据
        List<Map<String, Object>> result = viewDao.queryRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null, // 不排除任何用户创建的记录
            offset,
            pageSize
        );

        logger.info("queryByFieldAndValue - 查询结果数量: {}", result.size());
        if (!result.isEmpty()) {
            logger.info("queryByFieldAndValue - 第一条记录: {}", result.get(0));
        }

        // 5. 查询总数
        long total = viewDao.countRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null
        );

        logger.info("queryByFieldAndValue - 总记录数: {}", total);

        // 6. 转 bean
        List<Issue> issueList = result.stream().map(map -> BeanUtil.toBeanIgnoreError(map, Issue.class)).collect(Collectors.toList());

        // 转换字段格式，确保返回给前端的是字符串格式
        issueList.forEach(this::convertFieldsToStringForList);

        // 7. 构造 PageInfo
        PageInfo<Issue> pageInfo = new PageInfo<>(issueList);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) ((total + pageSize - 1) / pageSize));
        pageInfo.setIsFirstPage(pageNum == 1);
        pageInfo.setIsLastPage(pageNum >= pageInfo.getPages());
        pageInfo.setHasPreviousPage(pageNum > 1);
        pageInfo.setHasNextPage(pageNum < pageInfo.getPages());

        logger.info("queryByFieldAndValue - 分页信息: pageNum={}, pageSize={}, total={}, pages={}, hasNextPage={}", 
                 pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.isHasNextPage());

        return pageInfo;
    }

    /**
     * 使用视图过滤的查询逻辑，支持分页
     */
    private PageInfo<Issue> listWithViewFilterLogic(IssueParam param, int pageNum, int pageSize) {
        try {
            // 获取视图过滤参数
            Map<String, Object> filterParams = viewFilterService.getFilterParamsByViewId(
                param.getViewId(), param.getProjectId().toString());

            if (filterParams == null) {
                // 如果获取过滤参数失败，回退到简单查询
                logger.warn("获取视图过滤参数失败，回退到简单查询");
                return list(param, pageNum, pageSize);
            }

            // 使用BeanSearcher进行查询
            Class<?> issueClass = Class.forName("com.hu.oneclick.model.entity.Issue");
            List<Map<String, Object>> result = mapSearcher.searchAll(issueClass, filterParams);

            // 转换为 Issue 对象
            List<Issue> issueList = result.stream()
                .map(map -> BeanUtil.toBeanIgnoreError(map, Issue.class))
                .collect(Collectors.toList());

            // 手动分页处理
            int total = issueList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);

            List<Issue> pageData = new ArrayList<>();
            if (startIndex < total) {
                pageData = issueList.subList(startIndex, endIndex);
            }

            // 转换字段格式，确保返回给前端的是字符串格式
            pageData.forEach(this::convertFieldsToStringForList);

            PageInfo<Issue> pageInfo = new PageInfo<>(pageData);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setTotal(total);
            pageInfo.setPages((total + pageSize - 1) / pageSize);
            return pageInfo;
        } catch (Exception e) {
            logger.error("视图过滤查询失败，回退到简单查询", e);
            return list(param, pageNum, pageSize);
        }
    }

    /**
     * 简单的分页查询方法
     */
    private PageInfo<Issue> list(IssueParam param, int pageNum, int pageSize) {
        // 手动设置分页参数
        PageUtil.startPage(pageNum, pageSize);
        List<Issue> dataList = this.list(param);

         // 转换字段格式，确保返回给前端的是字符串格式
        dataList.forEach(this::convertFieldsToStringForList);

        return PageInfo.of(dataList);
    }

    public PageInfo<Issue> pageList(IssueParam param, int pageNum, int pageSize) {
        // 手动设置分页参数
        PageUtil.startPage(pageNum, pageSize);
        List<Issue> dataList = this.list(param);

        // 转换字段格式，确保返回给前端的是字符串格式
        dataList.forEach(this::convertFieldsToStringForList);

        return PageInfo.of(dataList);
    }

    public Issue getIssueById(Long id) {
        Issue issue = this.baseMapper.selectById(id);

        if (issue == null) {
            throw new BaseException("Issue查询不到");
        }

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();
        System.out.println("=== getIssueById - 用户时区: " + userTimezone + " ===");

        // 计算duration（基于UTC时间）
        System.out.println("=== getIssueById - 准备计算duration ===");
        issueDurationCalculator.calculateDuration(issue, userTimezone);
        System.out.println("=== getIssueById - duration计算完成，值: " + issue.getDuration() + " ===");

        // 将UTC时间转换为用户本地时间
        System.out.println("=== getIssueById - 开始转换UTC时间为用户本地时间 ===");
        System.out.println("=== 转换前时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");

        issueTimeConverter.convertUTCToLocalTime(issue, userTimezone);

        System.out.println("=== 转换后时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");

        // 转换字段格式，确保返回给前端的是字符串格式
        convertFieldsToStringForEdit(issue);

        System.out.println("=== getIssueById - 返回给前端的最终时间信息 ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");
        System.out.println("=== updateTime: " + issue.getUpdateTime() + " ===");
        System.out.println("=== planFixDate: " + issue.getPlanFixDate() + " ===");
        System.out.println("=== duration: " + issue.getDuration() + " 小时 ===");

        return issue;
    }




}