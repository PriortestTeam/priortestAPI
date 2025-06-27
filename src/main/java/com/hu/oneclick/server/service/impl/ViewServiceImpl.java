package com.hu.oneclick.server.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.constant.FieldConstant;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.dao.CustomFieldsDao;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.dao.ViewDownChildParamsDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.model.param.ViewGetSubViewRecordParam;
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
public class ViewServiceImpl extends ServiceImpl<ViewDao, View> implements ViewService {

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
    @Resource
    CustomFieldsDao customFieldsDao;
    @Resource
    TestCaseDao testCaseDao;

    @Override
    public Resp<View> queryById(String id) {
        View queryView = viewDao.queryById(id, jwtUserService.getMasterId());
        //防止mybatis 缓存数据变更
        View view = new View();
        BeanUtils.copyProperties(queryView, view);
        view.setOneFilters(TwoConstant.convertToList(view.getFilter(), OneFilter.class));
        view.setFilter("");
//        view.setParentTitle(queryParentTitle(view.getParentId()));
        return new Resp.Builder<View>().setData(view).ok();
    }

    @Override
    public List<View> list(View view) {
        if (StringUtils.isEmpty(view.getScopeName())) {
            throw new BaseException(StrUtil.format("范围不能为空。"));
        } else if (StringUtils.isEmpty(view.getProjectId())) {
            throw new BaseException(StrUtil.format("项目ID不能为空。"));
        }
        view.setCreateUserId(Long.valueOf(jwtUserService.getMasterId()));
        List<View> list = viewDao.queryAll(view);
        for (View v : list) {
            v.setOneFilters(v.getOneFilters());
            v.setFilter(null);
        }
        return list;
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
        return JSON.parseArray(s, View.class);
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
//            view.verify();
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

            Result.verifyDoesExist(queryByTitle(projectId, view.getTitle(), view.getScopeName()), view.getTitle());
//            view.setCreateUserId(masterId);
            view.setProjectId(projectId);
            view.setCreater(sysUser.getUserName());
            view.setFilter(view.getFilterByManual(view.getOneFilters()));
            return Result.addResult(viewDao.insert(view));
        } catch (BizException e) {
            logger.error("class: ViewServiceImpl#addView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public View updateView(View view) {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        if (StringUtils.isEmpty(projectId)) {
            throw new BaseException(StrUtil.format("请选择一个项目"));
        }
        if (view.getViewType() != null && view.getViewType() != 0) {
            throw new BizException("40003", "视图类型-参数值非法");
        }

//        //修改视图名称要进行验证
//        if (view.getTitle() != null) {
//            Result.verifyDoesExist(queryByTitle(projectId, view.getTitle(), view.getScopeName()), view.getTitle());
//        }
        view.setFilter(view.getFilterByManual(view.getOneFilters()));
        baseMapper.updateById(view);
        return view;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteView(String id) {
        try {
            View view = viewDao.queryById(id, jwtUserService.getMasterId());
            if (view == null) {
                return Result.deleteResult(0);
            }
            // sysPermissionService.viewPermission(OneConstant.PERMISSION.DELETE, convertPermission(view.getScope()));
            return Result.deleteResult(viewDao.deleteByPrimaryKey(jwtUserService.getMasterId(), id));
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
    public List<View> queryViewParents(String scope, String projectId) {
        if (StringUtils.isEmpty(scope)) {
            throw new BaseException(StrUtil.format("scope{}", SysConstantEnum.PARAM_EMPTY.getValue()));
        }
        String masterId = jwtUserService.getMasterId();
        List<View> result = viewDao.queryViewParents(masterId, scope, projectId);
        return result;
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

        List<ViewTreeDto> auto_view = result.stream().filter(obj -> obj.getIsAuto() == 1).collect(Collectors.toList());
        Map<String, Object> cond;
        List<String> child;
        for (ViewTreeDto viewTreeDto : auto_view) {
            cond = new HashMap<>();
            cond.put("type", viewTreeDto.getOneFilters().get(0).getType());
            cond.put("fieldNameEn", viewTreeDto.getOneFilters().get(0).getFieldNameEn());
            cond.put("scopeId", viewTreeDto.getScopeId());
            cond.put("projectId", viewTreeDto.getProjectId());

            Map sfieldMap = viewDao.queryAutoView(cond);
            Map sfieldValue = JSON.parseObject(sfieldMap.get("possible_value").toString(), Map.class);

            child = new ArrayList<>();
            for (Object key : sfieldValue.keySet()) {
                child.add(sfieldValue.get(key).toString());
            }

            if (sfieldMap.get("possible_value_child") != null) {
                Map sfieldChildValue = JSON.parseObject(sfieldMap.get("possible_value_child").toString(), Map.class);
                for (Object key : sfieldChildValue.keySet()) {
                    child.add(sfieldChildValue.get(key).toString());
                }
            }

            viewTreeDto.setAutoViewChild(child);

        }

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
    private List<ViewTreeDto> childViewTreeRecursion(List<ViewTreeDto> treeAll, Long id) {
        List<ViewTreeDto> result = new ArrayList<>();
        treeAll.forEach(e -> {
            //取反
            if (!verifyParentId(e.getParentId())
                && e.getParentId().equals("" + id)) {
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
     * @return
     * @Param: [view]
     * @Author: MaSiyi
     * @Date: 2021/11/27
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public View addViewRE(View view) {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        if (StringUtils.isEmpty(projectId)) {
            throw new BaseException(StrUtil.format("请选择一个项目"));
        }
        if (view.getViewType() == null || view.getViewType() != 0) {
            throw new BizException("40003", "视图类型-参数值非法");
        }

        view.setProjectId(projectId);
        // 设置为子视图
        if (StrUtil.isNotBlank(view.getParentId())) {
            view.setLevel(1);
        }
        view.setFilter(view.getFilterByManual(view.getOneFilters()));

        // 自动子视图
        if (1 == view.getIsAuto() && view.getLevel() == 0) {
            // 查询项目范围内的自定义字段
            // 添加oneFilters集合
            // 保存子视图
            view.setFilter(JSON.toJSONString(view.getOneFilters()));
        }
        baseMapper.insert(view);
        return view;
        //设置sql
//            String sql = appendSql(oneFilter, view);
//
//            view.setSql(sql);
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
        String scope = view.getScopeName();
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
        stringBuilder.append("user_id = ").append(view.getCreateUserId());
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
            stringBuilder.append(filter.getFieldNameCn());
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

        // String sql = view.getSql();

        String filter = view.getFilter();
        String scope = view.getScopeName();
        switch (scope) {
            case FieldConstant.PROJECT:
                return new Resp.Builder<>().setData(new ArrayList<>()).ok();// Placeholder, replace with actual data retrieval
            case FieldConstant.FEATURE:
                return new Resp.Builder<>().setData(new ArrayList<>()).ok();// Placeholder, replace with actual data retrieval

            case FieldConstant.TESTCYCLE:
                return new Resp.Builder<>().setData(new ArrayList<>()).ok();// Placeholder, replace with actual data retrieval
            case FieldConstant.TESTCASE:
                return new Resp.Builder<>().setData(new ArrayList<>()).ok();// Placeholder, replace with actual data retrieval
            case FieldConstant.ISSUE:
                return new Resp.Builder<>().setData(new ArrayList<>()).ok();// Placeholder, replace with actual data retrieval
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

    @Override
    public Object findTestCaseLinkedSubview(int page, int offset, ViewGetSubViewRecordParam param) {
        String project_id = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        String field_name = StrUtil.toUnderlineCase(param.getFieldNameEn());

        IPage<TestCase> ipage = new Page<>(page - 1, offset);
        QueryWrapper<TestCase> query = Wrappers.query();
        query.eq(field_name, param.getValue());
        query.eq("project_id", project_id);
        IPage<TestCase> records = testCaseDao.selectPage(ipage, query);

        return new Resp.Builder<>().setData(PageUtil.manualPaging(records.getRecords())).ok();
    }
    @Override
    public Object findSubViewRecordByScopeName(int page, int pageSize, ViewGetSubViewRecordParam param) {
        if (param == null || StringUtils.isEmpty(param.getScopeName())) {
            return new Resp.Builder<Object>().buildResult("scopeName 参数不能为空");
        }

        String masterId = jwtUserService.getMasterId();
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();

        // 根据 scopeName 确定要查询的表名
        String tableName = getTableNameByScopeName(param.getScopeName());
        if (StringUtils.isEmpty(tableName)) {
            return new Resp.Builder<Object>().buildResult("不支持的 scopeName: " + param.getScopeName());
        }

        try {
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            // 使用 DAO 方法查询
            List<Map<String, Object>> result = viewDao.queryRecordsByScope(
                tableName,
                param.getFieldNameEn(),
                param.getValue(),
                projectId,
               null, // 不排除任何用户创建的记录
                offset,
                pageSize
            );

            return new Resp.Builder<Object>().setData(PageUtil.manualPaging(result)).ok();
        } catch (Exception e) {
            logger.error("查询子视图记录失败: " + e.getMessage(), e);
            return new Resp.Builder<Object>().buildResult("查询失败: " + e.getMessage());
        }
    }

    /**Add commentMore actions
     * 根据 scopeName 返回对应的表名
     */
    private String getTableNameByScopeName(String scopeName) {
        switch (scopeName) {
            case "故事":
                return "feature";
            case "测试用例":
                return "test_case";
            case "缺陷":
                return "issue";
            case "测试周期":
                return "test_cycle";
            default:
                return null;
        }
    }


}
