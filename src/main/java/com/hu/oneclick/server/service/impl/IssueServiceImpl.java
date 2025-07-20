package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.zhxu.bs.MapSearcher;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IssueServiceImpl extends ServiceImpl<IssueDao, Issue> implements IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

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
    public Issue add(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

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

        // 转换字符串字段为整数存储到数据库
        if (dto.getIsLegacy() != null) {
            issue.setIsLegacy(dto.getIsLegacyAsInteger());
        }
        if (dto.getFoundAfterRelease() != null) {
            issue.setFoundAfterRelease(dto.getFoundAfterReleaseAsInteger());
        }

        this.baseMapper.insert(issue);
        return issue;
    }

    @Override
    public Issue edit(IssueSaveDto dto) {
        Issue entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

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
         // 转换字符串字段为整数存储到数据库
        if (dto.getIsLegacy() != null) {
            issue.setIsLegacy(dto.getIsLegacyAsInteger());
        }
        if (dto.getFoundAfterRelease() != null) {
            issue.setFoundAfterRelease(dto.getFoundAfterReleaseAsInteger());
        }

        this.baseMapper.updateById(issue);
        return issue;
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
        return issue;
    }

    @Override
    public int studusedit(Issue issue,IssueStatusDto issueStatusDto) {
       issue.setFixVersion(issueStatusDto.getFixVersion());
       issue.setIssueStatus(issueStatusDto.getIssueStatus());
       issue.setVerifiedResult(issueStatusDto.getVerifiedResult());
        // 如果 status 是 关闭 时，设置 close_date 时间为此时
        if ("关闭".equals(issueStatusDto.getIssueStatus())) {
            issue.setCloseDate(new Date());
        }
       System.out.println(issue);

        return baseMapper.updateById(issue);
    }

    @Override
    public void clone(List<Long> ids) {
        List<Issue> issueList = new ArrayList<>();
        for (Long id : ids) {
            Issue issue = baseMapper.selectById(id);
            if (issue == null) {
                throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", id));
            }
            Issue issueClone = new Issue();
            BeanUtil.copyProperties(issue, issueClone);
            issueClone.setId(null);
            issueClone.setTitle(CloneFormatUtil.getCloneTitle(issueClone.getTitle()));
            issueList.add(issueClone);
        }
        // 批量克隆
        this.saveBatch(issueList);
    }

    @Override
    public Issue retrieveIssueStatusAsPerIssueId(Long projectId, Long issueId) {
        return this.getByIdAndProjectId(issueId, projectId);
    }

    /**
     * 转换字段格式：将 Integer 字段转换为 String 返回给前端
     */
    private void convertFieldsToString(Issue issue) {
        // 转换 isLegacy 字段 (Integer -> String)
        if (issue.getIsLegacy() != null) {
            issue.setIsLegacyStr(String.valueOf(issue.getIsLegacy()));
        } else {
            issue.setIsLegacyStr("0"); // 默认值
        }

        // 转换 foundAfterRelease 字段 (Integer -> String)
        if (issue.getFoundAfterRelease() != null) {
            issue.setFoundAfterReleaseStr(String.valueOf(issue.getFoundAfterRelease()));
        } else {
            issue.setFoundAfterReleaseStr("0"); // 默认值
        }
    }

    @Override
    public PageInfo<Issue> list(IssueParam param, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Issue> list = this.list(param.getQueryCondition());

        // 转换字段格式
        list.forEach(this::convertFieldsToString);

        return new PageInfo<>(list);
    }
    @Override
    public PageInfo<Issue> listWithViewFilter(IssueParam param, int pageNum, int pageSize) {
        PageInfo<Issue> pageInfo = listWithViewFilterLogic(param, pageNum, pageSize);

        // 转换字段格式
        pageInfo.getList().forEach(this::convertFieldsToString);

        return pageInfo;
    }
    @Override
    public PageInfo<Issue> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize) {
        try {
            // 获取视图过滤参数
            Map<String, Object> filterParams = viewFilterService.getFilterParamsByViewId(viewId, projectId);

            if (filterParams == null) {
                logger.warn("获取视图过滤参数失败，回退到简单查询");
                IssueParam param = new IssueParam();
                param.setProjectId(Long.parseLong(projectId));
                return list(param, pageNum, pageSize);
            }

            // 使用BeanSearcher进行查询
            Class<?> issueClass = Class.forName("com.hu.oneclick.model.entity.Issue");
            List<Map<String, Object>> result = mapSearcher.searchAll(issueClass, filterParams);

            // 转换为 Issue 对象
            List<Issue> issueList = result.stream()
                .map(map -> BeanUtil.toBeanIgnoreError(map, Issue.class))
                .collect(Collectors.toList());

            // 转换字段格式
            issueList.forEach(this::convertFieldsToString);

            // 手动分页处理
            int total = issueList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);

            List<Issue> pageData = new ArrayList<>();
            if (startIndex < total) {
                pageData = issueList.subList(startIndex, endIndex);
            }

            // 构建分页信息
            PageInfo<Issue> pageInfo = new PageInfo<>();
            pageInfo.setList(pageData);
            pageInfo.setTotal(total);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setPages((int) Math.ceil((double) total / pageSize));
            pageInfo.setHasNextPage(pageNum < pageInfo.getPages());

            logger.info("listWithBeanSearcher - 总记录数: {}, 当前页: {}, 每页大小: {}, 总页数: {}, 当前页数据量: {}", 
                     total, pageNum, pageSize, pageInfo.getPages(), pageData.size());

            return pageInfo;

        } catch (Exception e) {
            logger.error("使用BeanSearcher查询失败: " + e.getMessage(), e);
            // 出现异常时回退到普通查询
            IssueParam param = new IssueParam();
            param.setProjectId(Long.parseLong(projectId));
            return list(param, pageNum, pageSize);
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
        List<Issue> issueList = result.stream()
            .map(map -> BeanUtil.toBeanIgnoreError(map, Issue.class))
            .collect(Collectors.toList());

        // 转换字段格式
        issueList.forEach(this::convertFieldsToString);

        // 手动分页处理
        int total = issueList.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        // 构建分页信息
        PageInfo<Issue> pageInfo = new PageInfo<>();
        pageInfo.setList(issueList);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages((int) Math.ceil((double) total / pageSize));
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
    private List<Issue> list(IssueParam param) {
        QueryWrapper<Issue> queryWrapper = new QueryWrapper<>();
        if (param.getProjectId() != null) {
            queryWrapper.eq("project_id", param.getProjectId());
        }
        // 添加其他查询条件
        return this.baseMapper.selectList(queryWrapper);
    }
}