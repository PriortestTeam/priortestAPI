package com.hu.oneclick.server.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.FieldConstant;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.dao.ViewDownChildParamsDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.*;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.server.service.*;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingyang
 */
@Service
public class ViewServiceImpl implements ViewService {

    private final static Logger logger = LoggerFactory.getLogger(ViewServiceImpl.class);

    @Resource
    private ViewDao viewDao;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private ViewDownChildParamsDao viewDownChildParamsDao;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private SysCustomFieldService sysCustomFieldService;
    @Resource
    private CustomFieldDataService customFieldDataService;
    @Resource
    private ProjectService projectService;
    @Resource
    private FeatureService featureService;
    @Resource
    private TestCycleService testCycleService;
    @Resource
    private TestCaseService testCaseService;
    @Resource
    private IssueService issueService;

    @Override
    public Resp<View> queryById(String id) {
        View queryView = viewDao.queryById(id, jwtUserService.getMasterId());
        //防止mybatis 缓存数据变更
        View view = new View();
        BeanUtils.copyProperties(queryView, view);
        view.setOneFilters(TwoConstant.convertToList(view.getFilter(), OneFilter.class));
        view.setFilter("");
        view.setParentTitle(queryParentTitle(view.getParentId()));
        return new Resp.Builder<View>().setData(view).ok();
    }

    @Override
    public Resp<List<View>> list(View view) {
        if (StringUtils.isEmpty(view.getScope())) {
            return new Resp.Builder<List<View>>().buildResult("scope 不能为空。");
        } else if (StringUtils.isEmpty(view.getProjectId())) {
            return new Resp.Builder<List<View>>().buildResult("项目ID不能为空。");
        }
        sysPermissionService.viewPermission(null, convertPermission(view.getScope()));
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        view.verifyUserType(sysUser.getManager());
        view.setUserId(jwtUserService.getMasterId());

        List<View> queryViews = viewDao.queryAll(view);

        //防止mybatis 缓存数据变更
        List<View> views = coverViews(queryViews);

        views.forEach(e -> {
            e.setParentTitle(queryParentTitle(e.getParentId()));
            e.setOneFilters(TwoConstant.convertToList(e.getFilter(), OneFilter.class));
            e.setFilter("");
        });
        return new Resp.Builder<List<View>>().setData(views).total(queryViews).ok();
    }

    /**
     * 查询父title
     *
     * @param parentId
     * @return
     */
    private String queryParentTitle(String parentId) {
        if (!"0".equals(parentId) && StringUtils.isNotEmpty(parentId)) {
            return viewDao.queryTitleByParentId(parentId);
        }
        return null;
    }

    /**
     * 深copy
     *
     * @param queryViews
     * @return
     */
    private List<View> coverViews(List<View> queryViews) {
        String s = JSONObject.toJSONString(queryViews);
        return JSONObject.parseArray(s, View.class);
    }


    @Override
    public Resp<String> queryDoesExistByTitle(String projectId, String title, String scope) {
        try {
            Result.verifyDoesExist(queryByTitle(projectId, title, scope), title);
            return new Resp.Builder<String>().ok();
        } catch (BizException e) {
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addView(View view) {
        try {
            view.verify();
//            sysPermissionService.viewPermission(OneConstant.PERMISSION.ADD, convertPermission(view.getScope()));
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            String projectId = sysUser.getUserUseOpenProject().getProjectId();
            String masterId = jwtUserService.getMasterId();
            if (StringUtils.isEmpty(projectId)) {
                return new Resp.Builder<String>().buildResult("请选择一个项目");
            }
            //检查是否关联父级，如果关联父级，则查询父级 是否还可进行关联，最大级别3
            if (StringUtils.isNotEmpty(view.getParentId())) {
                View parentView = viewDao.queryById(view.getParentId(), masterId);
                int level = parentView.getLevel() + 1;
                if (level > OneConstant.COMMON.VIEW_PARENT_CHILDREN_LEVEL) {
                    return new Resp.Builder<String>().buildResult("您已超出最大层级，不可添加。");
                }
            }

            Result.verifyDoesExist(queryByTitle(projectId, view.getTitle(), view.getScope()), view.getTitle());
            view.setUserId(masterId);
            view.setProjectId(projectId);
            view.setOwner(sysUser.getUserName());
            return Result.addResult(viewDao.insert(view));
        } catch (BizException e) {
            logger.error("class: ViewServiceImpl#addView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateView(View view) {
        try {
            sysPermissionService.viewPermission(OneConstant.PERMISSION.EDIT, convertPermission(view.getScope()));
            view.verifyOneFilter();
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            String projectId = sysUser.getUserUseOpenProject().getProjectId();
            if (StringUtils.isEmpty(projectId)) {
                return new Resp.Builder<String>().buildResult("请选择一个项目");
            }
            //修改视图名称要进行验证
            if (view.getTitle() != null) {
                Result.verifyDoesExist(queryByTitle(projectId, view.getTitle(), view.getScope()), view.getTitle());
            }

            view.setModifyUser(sysUser.getUserName());
            view.setModifyDate(new Date());
            return Result.updateResult(viewDao.update(view));
        } catch (BizException e) {
            logger.error("class: ViewServiceImpl#updateView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteView(String id) {
        try {
            View view = viewDao.queryById(id, jwtUserService.getMasterId());
            if (view == null) {
                return Result.deleteResult(0);
            }
            sysPermissionService.viewPermission(OneConstant.PERMISSION.DELETE, convertPermission(view.getScope()));
            return Result.deleteResult(viewDao.deleteById(jwtUserService.getMasterId(), id));
        } catch (BizException e) {
            logger.error("class: ViewServiceImpl#deleteView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }


    @Override
    public Resp<List<ViewScopeChildParams>> getViewScopeChildParams(String scope) {
        String key = OneConstant.REDIS_KEY_PREFIX.viewScopeDown + "-" + scope;
        RBucket<String> bucket = redissonClient.getBucket(key);
        //缓存存在返回
        String s = bucket.get();
        if (!StringUtils.isEmpty(s)) {
            List<ViewScopeChildParams> childParams = JSONArray.parseArray(s, ViewScopeChildParams.class);
            return new Resp.Builder<List<ViewScopeChildParams>>().setData(childParams).totalSize(childParams.size()).ok();
        }
        ViewDownChildParams viewDownChildParams = viewDownChildParamsDao.queryByScope(scope);
        if (viewDownChildParams == null) {
            return new Resp.Builder<List<ViewScopeChildParams>>().buildResult("scope 无效");
        }
        String defaultValues = viewDownChildParams.getDefaultValues();
        List<ViewScopeChildParams> childParams = JSONArray.parseArray(defaultValues, ViewScopeChildParams.class);
        bucket.set(defaultValues);
        return new Resp.Builder<List<ViewScopeChildParams>>().setData(childParams).totalSize(childParams.size()).ok();
    }

    @Override
    public Resp<List<View>> queryViewParents(String scope, String projectId) {
        if (StringUtils.isEmpty(scope)) {
            return new Resp.Builder<List<View>>().buildResult("scope" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        String masterId = jwtUserService.getMasterId();
        List<View> result = viewDao.queryViewParents(masterId, scope, projectId);
        return new Resp.Builder<List<View>>().setData(result).totalSize(result.size()).ok();
    }

    /**
     * 查询树结构view
     *
     * @param scope
     * @return
     */
    @Override
    public Resp<List<ViewTreeDto>> queryViewTrees(String scope) {
        if (StringUtils.isEmpty(scope)) {
            return new Resp.Builder<List<ViewTreeDto>>().buildResult("scope" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        String masterId = jwtUserService.getMasterId();
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        UserUseOpenProject openProject = sysUser.getUserUseOpenProject();
        List<ViewTreeDto> treeAll = null;
        if (ObjectUtil.isNotNull(openProject)) {
            String projectId = openProject.getProjectId();
            treeAll = viewDao.queryViewByScopeAll(masterId, projectId, scope);
        }

        //递归
        List<ViewTreeDto> result = viewTreeRecursion(treeAll);
        return new Resp.Builder<List<ViewTreeDto>>().setData(result).ok();
    }

    /**
     * queryViewTrees 递归
     */
    private List<ViewTreeDto> viewTreeRecursion(List<ViewTreeDto> treeAll) {
        if (treeAll == null || treeAll.size() <= 0) {
            return new ArrayList<>();
        }
        List<ViewTreeDto> result = new ArrayList<>();
        //循环找父级
        treeAll.forEach(e -> {
            if (verifyParentId(e.getParentId())) {
                e.setChildViews(childViewTreeRecursion(treeAll, e.getId()));
                result.add(e);
            }
        });
        return result;
    }

    /**
     * 递归自己
     *
     * @param treeAll,parentId
     * @return
     */
    private List<ViewTreeDto> childViewTreeRecursion(List<ViewTreeDto> treeAll, String id) {
        List<ViewTreeDto> result = new ArrayList<>();
        treeAll.forEach(e -> {
            //取反
            if (!verifyParentId(e.getParentId())
                    && e.getParentId().equals(id)) {
                e.setChildViews(childViewTreeRecursion(treeAll, e.getId()));
                result.add(e);
            }
        });
        return result;
    }

    private boolean verifyParentId(String parentId) {
        return StringUtils.isEmpty(parentId) || "0".equals(parentId);
    }

    /**
     * 查询项目是否存在
     *
     * @param title
     * @return
     */
    private Integer queryByTitle(String projectId, String title, String scope) {
        if (StringUtils.isEmpty(title)) {
            return null;
        }
        if (viewDao.queryTitleIsExist(jwtUserService.getMasterId(), title, projectId, scope) > 0) {
            return 1;
        }
        return null;
    }

    /**
     * 根据用户选定的scope 转换成权限标识符
     *
     * @return
     */
    private String convertPermission(String scope) {
        return TwoConstant.convertPermission(scope);
    }

    /**
     * 添加视图
     *
     * @param view
     * @Param: [view]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/27
     */
    @Override
    public Resp<String> addViewRE(View view) {
        try {
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            String projectId = sysUser.getUserUseOpenProject().getProjectId();
            String masterId = jwtUserService.getMasterId();
            if (StringUtils.isEmpty(projectId)) {
                return new Resp.Builder<String>().buildResult("请选择一个项目");
            }

            List<OneFilter> oneFilter = view.getOneFilters();
            view.setFilter(JSON.toJSONString(oneFilter));
            view.setUserId(masterId);
            view.setProjectId(projectId);
            view.setOwner(sysUser.getUserName());
            //设置sql
            String sql = appendSql(oneFilter, view);

            view.setSql(sql);
            return Result.addResult(viewDao.insert(view));
        } catch (BizException e) {
            logger.error("class: ViewServiceImpl#addView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }

    }

    /**
     * 设置sql
     *
     * @Param: [oneFilter, view]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/11/29
     */
    @Deprecated
    private String appendSql(List<OneFilter> oneFilter, View view) {
        //过滤系统字段
        List<OneFilter> collect = oneFilter.stream().filter(f -> "sys".equals(f.getCustomType())).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder("select * from ");
        String scope = view.getScope();
        switch (scope) {
            case FieldConstant.PROJECT:
                stringBuilder.append("project ");
                break;
            case FieldConstant.FEATURE:
                stringBuilder.append("feature ");
                break;
            case FieldConstant.TESTCYCLE:
                stringBuilder.append("test_cycle ");
                break;
            case FieldConstant.TESTCASE:
                stringBuilder.append("test_case ");
                break;
            case FieldConstant.ISSUE:
                stringBuilder.append("issue ");
                break;
            default:

        }
        stringBuilder.append("where ");
        stringBuilder.append("user_id = ").append(view.getUserId());
        if (!scope.equals(FieldConstant.PROJECT)) {
            stringBuilder.append(" and ").append("project_id = ").append(view.getProjectId());
        }
        for (OneFilter filter : collect) {
            //format字符串
            filter.verify();

            String andOr = filter.getAndOr();
            if ("and".equals(andOr)) {
                stringBuilder.append(" and ");
            } else {
                stringBuilder.append(" or ");
            }
            String condition = filter.getCondition();
            /**
             *  Is 等于
             *   IsNot 不等于
             *   IsEmpty 为空
             *   IsNotEmpty 不为空
             *   MoreThan 大于
             *   LessThan 小于
             *   Include 包含
             *   Exclude 不包含
             */
            stringBuilder.append(filter.getFieldName());
            switch (condition) {
                case "Is":
                    stringBuilder.append(" = ");
                    stringBuilder.append("'").append(filter.getTextVal()).append("'");
                    break;
                case "IsNot":
                    stringBuilder.append(" != ");
                    stringBuilder.append("'").append(filter.getTextVal()).append("'");
                    break;
                case "IsEmpty":
                    stringBuilder.append(" is null ");
                    break;
                case "IsNotEmpty":
                    stringBuilder.append(" is not null ");
                    break;
                case "MoreThan":
                    stringBuilder.append(" > ");
                    stringBuilder.append("'").append(filter.getIntVal()).append("'");
                    break;
                case "LessThan":
                    stringBuilder.append(" < ");
                    stringBuilder.append("'").append(filter.getIntVal()).append("'");
                    break;
                case "Include":
                    stringBuilder.append("  in ( ");
                    //todo 待和前端商讨传数据的格式
                    stringBuilder.append("'").append(filter.getTextVal()).append("'");
                    stringBuilder.append("  ) ");
                    break;
                case "Exclude":
                    stringBuilder.append(" not in ( ");
                    stringBuilder.append("'").append(filter.getTextVal()).append("'");
                    stringBuilder.append("  ) ");
                    break;
                default:
            }


        }
        return stringBuilder.toString();
    }


    /**
     * 执行sql
     *
     * @Param: [sql]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/22
     */

    private String sql(String sql) {
        List<Object> objects = viewDao.sql(sql);

        return JSON.toJSONString(objects);
    }

    /**
     * 渲染视图
     *
     * @Param: [viewId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2022/1/3
     */
    @Override
    public Resp<Object> renderingView(String viewId) throws Exception {

        View view = viewDao.queryOnlyById(viewId);
        //执行系统字段
        String sql = view.getSql();


        String filter = view.getFilter();
        String scope = view.getScope();
        switch (scope) {
            case FieldConstant.PROJECT:
                List<Project> projectList = JSONArray.parseArray(this.sql(sql), Project.class);
                List<Project> projects = new ArrayList<>(projectList);

                List<OneFilter> oneFilters = JSONArray.parseArray(filter, OneFilter.class);


                //放置用户自定义查询的projecid
                Set<String> projectIdSet = new HashSet<>();
                for (int i = 0; i < oneFilters.size(); i++) {
                    OneFilter oneFilter = oneFilters.get(i);

                    String customType = oneFilter.getCustomType();
                    String fieldName = oneFilter.getFieldName();
                    if ("user".equals(customType)) {
                        //查询该用户下的该项目数据
                        List<CustomFieldData> customFieldDatas = customFieldDataService.findAllByUserIdAndScope(FieldConstant.PROJECT, fieldName);

                        for (CustomFieldData customFieldData : customFieldDatas) {
                            oneFilter.verify();

                            if ("fString".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getTextVal())) {
                                    projectIdSet.add(customFieldData.getScopeId());
                                }
                            } else if ("fInteger".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getIntVal())) {
                                    projectIdSet.add(customFieldData.getScopeId());
                                }
                            } else if ("fDateTime".equals(oneFilter.getType())) {

                            }
                        }
                    }


                }
                for (String projectId : projectIdSet) {
                    if (!projects.stream().map(Project::getId).collect(Collectors.toSet()).contains(projectId)) {
                        Project data = projectService.queryById(projectId).getData();
                        projects.add(data);
                    }
                }
                return new Resp.Builder<>().setData(projects).ok();

            case FieldConstant.FEATURE:
                List<Feature> featureList = JSONArray.parseArray(this.sql(sql), Feature.class);
                List<Feature> features = new ArrayList<>(featureList);

                oneFilters = JSONArray.parseArray(filter, OneFilter.class);


                //放置用户自定义查询
                Set<String> featureIdSet = new HashSet<>();
                for (int i = 0; i < oneFilters.size(); i++) {
                    OneFilter oneFilter = oneFilters.get(i);

                    String customType = oneFilter.getCustomType();
                    String fieldName = oneFilter.getFieldName();
                    if ("user".equals(customType)) {
                        //查询该用户下的该项目数据
                        List<CustomFieldData> customFieldDatas = customFieldDataService.findAllByUserIdAndScope(FieldConstant.FEATURE, fieldName);

                        for (CustomFieldData customFieldData : customFieldDatas) {
                            oneFilter.verify();

                            if ("fString".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getTextVal())) {
                                    featureIdSet.add(customFieldData.getScopeId());
                                }
                            } else if ("fInteger".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getIntVal())) {
                                    featureIdSet.add(customFieldData.getScopeId());
                                }
                            } else if ("fDateTime".equals(oneFilter.getType())) {

                            }
                        }
                    }


                }
                for (String featureId : featureIdSet) {
                    if (!features.stream().map(Feature::getId).collect(Collectors.toSet()).contains(featureId)) {
                        Feature data = featureService.info(Long.valueOf(featureId));
                        features.add(data);
                    }
                }
                return new Resp.Builder<>().setData(features).ok();
            case FieldConstant.TESTCYCLE:
                List<TestCycle> testCycles = JSONArray.parseArray(this.sql(sql), TestCycle.class);
                List<TestCycle> cycles = new ArrayList<>(testCycles);
                oneFilters = JSONArray.parseArray(filter, OneFilter.class);


                //放置用户自定义查询
                Set<String> testCycleIds = new HashSet<>();
                for (int i = 0; i < oneFilters.size(); i++) {
                    OneFilter oneFilter = oneFilters.get(i);

                    String customType = oneFilter.getCustomType();
                    String fieldName = oneFilter.getFieldName();
                    if ("user".equals(customType)) {
                        //查询该用户下的该项目数据
                        List<CustomFieldData> customFieldDatas = customFieldDataService.findAllByUserIdAndScope(FieldConstant.PROJECT, fieldName);

                        for (CustomFieldData customFieldData : customFieldDatas) {
                            oneFilter.verify();

                            if ("fString".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getTextVal())) {
                                    testCycleIds.add(customFieldData.getScopeId());
                                }
                            } else if ("fInteger".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getIntVal())) {
                                    testCycleIds.add(customFieldData.getScopeId());
                                }
                            } else if ("fDateTime".equals(oneFilter.getType())) {

                            }
                        }
                    }


                }
                for (String testCycleId : testCycleIds) {
                    if (!cycles.stream().map(TestCycle::getId).collect(Collectors.toSet()).contains(testCycleId)) {
                        TestCycle data = testCycleService.queryById(testCycleId).getData();
                        cycles.add(data);
                    }
                }
                return new Resp.Builder<>().setData(cycles).ok();
            case FieldConstant.TESTCASE:
                List<TestCase> testCases = JSONArray.parseArray(this.sql(sql), TestCase.class);
                List<TestCase> testCaseList = new ArrayList<>(testCases);

                oneFilters = JSONArray.parseArray(filter, OneFilter.class);


                //放置用户自定义查询的
                Set<String> testCaseIds = new HashSet<>();
                for (int i = 0; i < oneFilters.size(); i++) {
                    OneFilter oneFilter = oneFilters.get(i);

                    String customType = oneFilter.getCustomType();
                    String fieldName = oneFilter.getFieldName();
                    if ("user".equals(customType)) {
                        //查询该用户下的该项目数据
                        List<CustomFieldData> customFieldDatas = customFieldDataService.findAllByUserIdAndScope(FieldConstant.PROJECT, fieldName);

                        for (CustomFieldData customFieldData : customFieldDatas) {
                            oneFilter.verify();

                            if ("fString".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getTextVal())) {
                                    testCaseIds.add(customFieldData.getScopeId());
                                }
                            } else if ("fInteger".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getIntVal())) {
                                    testCaseIds.add(customFieldData.getScopeId());
                                }
                            } else if ("fDateTime".equals(oneFilter.getType())) {

                            }
                        }
                    }


                }
                for (String testCaseId : testCaseIds) {
                    if (!testCaseList.stream().map(TestCase::getId).collect(Collectors.toSet()).contains(testCaseId)) {
                        TestCase data = testCaseService.queryById(Convert.toLong(testCaseId)).getData();
                        testCaseList.add(data);
                    }
                }
                return new Resp.Builder<>().setData(testCaseList).ok();
            case FieldConstant.ISSUE:
                List<Issue> issues = JSONArray.parseArray(this.sql(sql), Issue.class);
                List<Issue> issueList = new ArrayList<>(issues);

                oneFilters = JSONArray.parseArray(filter, OneFilter.class);


                //放置用户自定义查询的
                Set<String> issueIdSet = new HashSet<>();
                for (int i = 0; i < oneFilters.size(); i++) {
                    OneFilter oneFilter = oneFilters.get(i);

                    String customType = oneFilter.getCustomType();
                    String fieldName = oneFilter.getFieldName();
                    if ("user".equals(customType)) {
                        //查询该用户下的该项目数据
                        List<CustomFieldData> customFieldDatas = customFieldDataService.findAllByUserIdAndScope(FieldConstant.PROJECT, fieldName);

                        for (CustomFieldData customFieldData : customFieldDatas) {
                            oneFilter.verify();

                            if ("fString".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getTextVal())) {
                                    issueIdSet.add(customFieldData.getScopeId());
                                }
                            } else if ("fInteger".equals(oneFilter.getType())) {
                                if (customFieldData.getValueData().equals(oneFilter.getIntVal())) {
                                    issueIdSet.add(customFieldData.getScopeId());
                                }
                            } else if ("fDateTime".equals(oneFilter.getType())) {

                            }
                        }
                    }


                }
                for (String issueId : issueIdSet) {
                    if (!issueList.stream().map(Issue::getId).collect(Collectors.toSet()).contains(issueId)) {
//                        Issue data = issueService.queryById(issueId).getData();
                        issueList.add(new Issue());
                    }
                }
                return new Resp.Builder<>().setData(issueList).ok();
            default:

        }


        return new Resp.Builder<>().ok();

    }


    /**
     * 获取filter字段
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.Object>
     * @Author: MaSiyi
     * @Date: 2021/12/23
     */
    @Override
    public Resp<Object> getViewFilter() {
        Resp<SysCustomFieldVo> filter = sysCustomFieldService.getSysCustomField("filter");
        SysCustomFieldVo data = filter.getData();
        List<String> mergeValues = data.getMergeValues();
        return new Resp.Builder<>().setData(filterFormat(mergeValues)).ok();
    }

    /**
     * format filter
     *
     * @Param: [defaultValues]
     * @return: java.lang.Object
     * @Author: MaSiyi
     * @Date: 2021/12/23
     */
    private List<String> filterFormat(List<String> defaultValues) {
        for (int i = 0; i < defaultValues.size(); i++) {
            String def = defaultValues.get(i);
            /**
             *      * Is 等于
             *      * IsNot 不等于
             *      * IsEmpty 为空
             *      * IsNotEmpty 不为空
             *      * MoreThan 大于
             *      * LessThan 小于
             *      * Include 包含
             *      * Exclude 不包含
             */
            switch (def.trim()) {
                case "等于":
                    defaultValues.set(i, def + ",Is");
                    break;
                case "不等于":
                    defaultValues.set(i, def + ",IsNot");
                    break;
                case "为空":
                    defaultValues.set(i, def + ",IsEmpty");
                    break;
                case "不为空":
                    defaultValues.set(i, def + ",IsNotEmpty");
                    break;
                case "大于":
                    defaultValues.set(i, def + ",MoreThan");
                    break;
                case "小于":
                    defaultValues.set(i, def + ",LessThan");
                    break;
                case "包含":
                    defaultValues.set(i, def + ",Include");
                    break;
                case "不包含":
                    defaultValues.set(i, def + ",Exclude");
                    break;
                default:
            }
        }
        return defaultValues;
    }

    /**
     * 根据范围搜索所有字段
     *
     * @param scope
     * @Param: [scope]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    @Override
    public Resp<Map<String, Object>> getViewScope(String scope) {
        //搜索所有系统字段
        HashMap<String, Object> map = new HashMap<>(3);
        Resp<List<SysCustomField>> allSysCustomField = customFieldDataService.getAllSysCustomField(scope);
        List<SysCustomField> sysCustomFieldData = allSysCustomField.getData();

        map.put("sysCustomField", sysCustomFieldData);

        //搜索所有用户字段
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        CustomFieldDto customFieldDto = new CustomFieldDto();
        customFieldDto.setScope(scope);
        customFieldDto.setProjectId(sysUser.getUserUseOpenProject().getProjectId());
        Resp<List<Object>> allCustomField = customFieldDataService.getAllCustomField(customFieldDto);
        List<Object> customField = allCustomField.getData();
        map.put("customField", customField);

        return new Resp.Builder<Map<String, Object>>().setData(map).ok();
    }
}
