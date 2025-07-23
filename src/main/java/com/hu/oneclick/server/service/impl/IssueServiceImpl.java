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
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();

        // 转换日期到UTC
        convertDatesToUTC(issue, userTimezone);

        // 处理新增的三个字段：introduced_version, is_legacy, found_after_release
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

        this.baseMapper.insert(issue);
        return issue;
    }

    @Override
    @Transactional
    public Issue edit(IssueSaveDto dto) {
        Issue entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }

        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

         // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();

        // 转换日期到UTC
        convertDatesToUTC(issue, userTimezone);

        // 处理新增的三个字段：introduced_version, is_legacy, found_after_release
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

        this.baseMapper.updateById(issue);

        // 转换字段格式，确保返回给前端的是字符串格式
        convertFieldsToString(issue);

        return issue;
    }

    /**
     * 转换所有日期字段到UTC
     */
    private void convertDatesToUTC(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 没有用户时区信息，跳过UTC转换 ===");
            return;
        }

        System.out.println("=== 开始UTC转换，用户时区: " + userTimezone + " ===");

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

            // 转换 createTime（如果是新建时，通常由数据库自动设置）
            if (issue.getCreateTime() != null) {
                Date originalTime = issue.getCreateTime();
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setCreateTime(utcTime);
                System.out.println("=== createTime转换: " + originalTime + " -> " + utcTime + " ===");
            }

            // 转换 planFixDate
            if (issue.getPlanFixDate() != null) {
                Date originalTime = issue.getPlanFixDate();
                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                issue.setPlanFixDate(utcTime);
                System.out.println("=== planFixDate转换: " + originalTime + " -> " + utcTime + " ===");
            }

            // 转换 issueExpand 中 attributes 里的日期字段
            convertAttributesDateFieldsToUTC(issue, userTZ);

        } catch (Exception e) {
            System.out.println("=== UTC转换失败: " + e.getMessage() + " ===");
        }
    }

    private void convertAttributesDateFieldsToUTC(Issue issue, TimeZone userTZ) {
        if (issue.getIssueExpand() == null || issue.getIssueExpand().isEmpty()) {
            return;
        }

        try {
            JSONObject issueExpandJson = JSONUtil.parseObj(issue.getIssueExpand());
            JSONArray attributes = issueExpandJson.getJSONArray("attributes");

            if (attributes != null && !attributes.isEmpty()) {
                for (Object attributeObj : attributes) {
                    if (attributeObj instanceof JSONObject) {
                        JSONObject attribute = (JSONObject) attributeObj;
                        String fieldType = attribute.getStr("fieldType");
                        String valueData = attribute.getStr("valueData");

                        if ("date".equals(fieldType) && valueData != null && !valueData.isEmpty()) {
                            try {
                                Date originalTime = cn.hutool.core.date.DateUtil.parse(valueData);
                                Date utcTime = convertLocalTimeToUTC(originalTime, userTZ);
                                attribute.set("valueData", cn.hutool.core.date.DateUtil.format(utcTime, "yyyy-MM-dd HH:mm:ss"));
                                System.out.println("=== Attribute日期字段转换: " + originalTime + " -> " + utcTime + " ===");
                            } catch (Exception e) {
                                System.out.println("=== Attribute日期字段转换失败: " + e.getMessage() + " ===");
                            }
                        }
                    }
                }
                issue.setIssueExpand(issueExpandJson.toString());
            }
        } catch (Exception e) {
            System.out.println("=== 处理attributes日期字段失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 将用户本地时间转换为UTC时间
     */
    private Date convertLocalTimeToUTC(Date localTime, TimeZone userTZ) {
        // 将本地时间解释为用户时区的时间，然后转换为UTC
        Calendar localCalendar = Calendar.getInstance(userTZ);
        localCalendar.setTime(localTime);

        // 获取UTC时间
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCalendar.setTimeInMillis(localCalendar.getTimeInMillis());

        return utcCalendar.getTime();
    }

    /**
     * 转换字段格式：确保数据格式正确
     * 由于使用了 @JsonProperty 注解，JSON 序列化会自动调用字符串格式的 getter 方法
     */
    private void convertFieldsToString(Issue issue) {
        System.out.println("=== convertFieldsToString开始 - Issue ID: " + issue.getId() + " ===");
        System.out.println("=== Duration进入convertFieldsToString前: " + issue.getDuration() + " ===");
        System.out.println("=== createTime: " + issue.getCreateTime() + " ===");


        // 在convertFieldsToString中调用calculateDuration
        System.out.println("=== 准备调用calculateDuration方法 ===");
        calculateDuration(issue);
        System.out.println("=== calculateDuration调用完成，Duration值: " + issue.getDuration() + " ===");

        // 确保 isLegacy 和 foundAfterRelease 不为null
        if (issue.getIsLegacy() == null) {
            issue.setIsLegacy(0);
        }
        if (issue.getFoundAfterRelease() == null) {
            issue.setFoundAfterRelease(0);
        }

        System.out.println("=== convertFieldsToString结束 - Issue ID: " + issue.getId() + ", Duration最终: " + issue.getDuration() + " ===");
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

        // 计算duration（基于UTC时间）
        calculateDuration(issue);

        // 获取用户时区并转换UTC时间为用户本地时间
        String userTimezone = TimezoneContext.getUserTimezone();
        convertUTCToLocalTime(issue, userTimezone);

        // 转换字段格式，确保返回给前端的是字符串格式
        convertFieldsToString(issue);
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
        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();

        List<Issue> issueList = new ArrayList<>();
        Date currentTime = new Date();  // 当前时间作为克隆时间

        for (Long id : ids) {
            Issue issue = baseMapper.selectById(id);
            if (issue == null) {
                throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", id));
            }
            Issue issueClone = new Issue();
            BeanUtil.copyProperties(issue, issueClone);

            // 重置关键字段
            issueClone.setId(null);  // 清空ID让数据库生成新ID
            issueClone.setTitle(CloneFormatUtil.getCloneTitle(issueClone.getTitle()));

            // 设置新的时间字段
            issueClone.setCreateTime(currentTime);      // 新的创建时间
            issueClone.setUpdateTime(currentTime);      // 新的更新时间
           // issueClone.setCloseDate(null);              // 关闭时间设置为空  Removed closeDate
            issueClone.setPlanFixDate(currentTime);     // 计划修复时间设置为当前时间

            // 设置状态为新建
            issueClone.setIssueStatus("新建");

            // 转换时间到UTC
            convertDatesToUTC(issueClone, userTimezone);

            issueList.add(issueClone);
        }

        // 批量保存克隆的Issue
        this.saveBatch(issueList);
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
            issueList.forEach(this::convertFieldsToString);

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
        issueList.forEach(this::convertFieldsToString);

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
            pageData.forEach(issue -> {
                System.out.println("=== 处理Issue ID: " + issue.getId() + " ===");
                // 计算duration
                calculateDuration(issue);
                // 转换字段格式
                convertFieldsToString(issue);
            });

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
        dataList.forEach(issue -> {
            System.out.println("=== 简单查询处理Issue ID: " + issue.getId() + " ===");
            // 计算duration
            calculateDuration(issue);
            // 转换字段格式
            convertFieldsToString(issue);
        });

        return PageInfo.of(dataList);
    }

    /**
     * 计算并设置Issue的duration（存活时长）
     * 注意：应该使用Issue的业务创建时间，而不是数据库记录的创建时间
     * 如果有关闭时间，使用关闭时间减去Issue创建时间
     * 如果没有关闭时间，使用当前时间减去Issue创建时间
     */
    private void calculateDuration(Issue issue) {
        calculateDuration(issue, null);
    }

    private void calculateDuration(Issue issue, String userTimezone) {
        System.out.println("=== Duration计算开始 ===");
        System.out.println("=== 用户时区: " + userTimezone + " ===");

        if (issue.getCreateTime() == null) {
            System.out.println("=== createTime为null，无法计算duration ===");
            issue.setDuration(0);
            return;
        }

        // 统一使用UTC时间进行计算，避免时区问题
        Date endTime;
       /* if (issue.getCloseDate() != null) { // removed closeDate references
            endTime = issue.getCloseDate();
            System.out.println("=== 使用closeDate作为结束时间 ===");
        } else {*/
            // 获取UTC当前时间
            Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            endTime = utcCalendar.getTime();
            System.out.println("=== 使用当前UTC时间计算duration ===");
        //}

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
        System.out.println("=== Duration计算完成 ===");
    }

     /**
     * 将UTC时间转换为用户本地时间（用于返回给前端显示）
     */
    private void convertUTCToLocalTime(Issue issue, String userTimezone) {
        if (userTimezone == null || userTimezone.isEmpty()) {
            System.out.println("=== 没有用户时区信息，跳过本地时间转换 ===");
            return;
        }

        System.out.println("=== 开始UTC到本地时间转换，用户时区: " + userTimezone + " ===");

        try {
            TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

            // 转换 createTime
            if (issue.getCreateTime() != null) {
                Date originalTime = issue.getCreateTime();
                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                issue.setCreateTime(localTime);
                System.out.println("=== createTime转换: " + originalTime + " -> " + localTime + " ===");
            }

            // 转换 planFixDate
            if (issue.getPlanFixDate() != null) {
                Date originalTime = issue.getPlanFixDate();
                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                issue.setPlanFixDate(localTime);
                System.out.println("=== planFixDate转换: " + originalTime + " -> " + localTime + " ===");
            }

            // 转换 issueExpand 中 attributes 里的日期字段
            convertAttributesDateFieldsToLocalTime(issue, userTZ);

        } catch (Exception e) {
            System.out.println("=== UTC到本地时间转换失败: " + e.getMessage() + " ===");
        }
    }

    private void convertAttributesDateFieldsToLocalTime(Issue issue, TimeZone userTZ) {
        if (issue.getIssueExpand() == null || issue.getIssueExpand().isEmpty()) {
            return;
        }

        try {
            JSONObject issueExpandJson = JSONUtil.parseObj(issue.getIssueExpand());
            JSONArray attributes = issueExpandJson.getJSONArray("attributes");

            if (attributes != null && !attributes.isEmpty()) {
                for (Object attributeObj : attributes) {
                    if (attributeObj instanceof JSONObject) {
                        JSONObject attribute = (JSONObject) attributeObj;
                        String fieldType = attribute.getStr("fieldType");
                        String valueData = attribute.getStr("valueData");

                        if ("date".equals(fieldType) && valueData != null && !valueData.isEmpty()) {
                            try {
                                Date originalTime = cn.hutool.core.date.DateUtil.parse(valueData);
                                Date localTime = convertUTCToLocalTime(originalTime, userTZ);
                                attribute.set("valueData", cn.hutool.core.date.DateUtil.format(localTime, "yyyy-MM-dd HH:mm:ss"));
                                System.out.println("=== Attribute日期字段转换: " + originalTime + " -> " + localTime + " ===");
                            } catch (Exception e) {
                                System.out.println("=== Attribute日期字段转换失败: " + e.getMessage() + " ===");
                            }
                        }
                    }
                }
                issue.setIssueExpand(issueExpandJson.toString());
            }
        } catch (Exception e) {
            System.out.println("=== 处理attributes日期字段失败: " + e.getMessage() + " ===");
        }
    }

    /**
     * 将UTC时间转换为用户本地时间
     */
    private Date convertUTCToLocalTime(Date utcTime, TimeZone userTZ) {
        // 将UTC时间解释为UTC时区的时间，然后转换为用户时区
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCalendar.setTime(utcTime);

        // 获取用户本地时间
        Calendar localCalendar = Calendar.getInstance(userTZ);
        localCalendar.setTimeInMillis(utcCalendar.getTimeInMillis());

        return localCalendar.getTime();
    }
}