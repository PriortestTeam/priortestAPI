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
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.FeatureJoinSprintDao;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.param.FeatureParam;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.FeatureService;
import com.hu.oneclick.server.service.QueryFilterService;
import com.hu.oneclick.server.service.ViewFilterService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.zhxu.bs.MapSearcher;
/**
 * @author qingyang
 */
@Service


public class FeatureServiceImpl extends ServiceImpl<FeatureDao, Feature> implements FeatureService {
    private final static Logger logger = LoggerFactory.getLogger(FeatureServiceImpl.class);
    @Resource
    private FeatureDao featureDao;
    @Resource
    private FeatureJoinSprintDao featureJoinSprintDao;
    @Resource
    private SprintDao sprintDao;
    @Resource
    private ViewDao viewDao;
    @Resource
    private MapSearcher mapSearcher;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private QueryFilterService queryFilterService;
    @Resource
    private CustomFieldDataService customFieldDataService;
    @Resource
    private ViewFilterService viewFilterService;
    @Override
    public List&lt;Feature> list(FeatureParam param) {
        return this.list(param.getQueryCondition();
    }
    @Override
    public Feature add(FeatureSaveDto dto) {
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas() {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas();
        }
        this.baseMapper.insert(feature);
        return feature;
    }
    @Override
    public Feature edit(FeatureSaveDto dto) {
        Feature entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId();
        if (null == entity) {
            throw new BaseException(StrUtil.format("故事查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId();
        }
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas() {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas();
        }
        this.baseMapper.updateById(feature);
        return feature;
    }
    @Override
    public Feature getByIdAndProjectId(Long id, Long projectId) {
        QueryWrapper<Feature> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Feature::getId, id)
                .eq(Feature::getProjectId, projectId);
        Feature feature = this.baseMapper.selectOne(queryWrapper);
        return feature;
    }
    @Override
    public Feature info(Long id) {
        Feature feature = baseMapper.selectById(id);
        if (feature == null) {
            throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id);
        }
        return feature;
    }
    @Override
    public void clone(List&lt;Long> ids) {
        List&lt;Feature> featureList = new ArrayList&lt;>();
        for (Long id : ids) {
            Feature feature = baseMapper.selectById(id);
            if (feature == null) {
                throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id);
            }
            Feature issueClone = new Feature();
            BeanUtil.copyProperties(feature, issueClone);
            issueClone.setId(null);
            issueClone.setTitle(CloneFormatUtil.getCloneTitle(feature.getTitle();
            featureList.add(issueClone);
        }
        // 批量克隆
        this.saveBatch(featureList);
    }
    @Override
    public List&lt;Map&lt;String, String>> getFeatureByTitle(String title, Long projectId) {
        QueryWrapper<Feature> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(Feature::getTitle, title)
                .eq(Feature::getProjectId, projectId);
        List&lt;Feature> features = baseMapper.selectList(queryWrapper);
        return features.stream()
                .map(feature -> {
                    Map&lt;String, String> map = new HashMap&lt;>();
                    map.put("id", feature.getId().toString();
                    map.put("title", feature.getTitle();
                    return map;
                })
                .collect(Collectors.toList();
    }
    //    @Override
//    public Resp<List&lt;LeftJoinDto>> queryTitles(String projectId, String title) {
//        List&lt;LeftJoinDto> select = featureDao.queryTitles(projectId, title, jwtUserService.getMasterId();
//        return new Resp.Builder<List&lt;LeftJoinDto>>().setData(select).totalSize(select.size().ok();
//    }
//    /**
//     * update feature custom
//     *
//     * @Param: [id]
//     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.entity.Feature>
//     * @Author: MaSiyi
//     * @Date: 2021/12/28
//     */
//    @Override
//    public Resp<Feature> queryById(String id) {
//        String masterId = jwtUserService.getMasterId();
//        Feature feature = featureDao.queryById(id, masterId);
//        //查询自定义数据
//        List&lt;CustomFieldData> customFieldData = customFieldDataService.featureRenderingCustom(id);
//        List&lt;Sprint> sprints = queryBindSprintList(id);
//        feature.setSprints(sprints);
//        feature.setStatus(analysisStatus(sprints);
//        feature.setCustomFieldDatas(customFieldData);
//        return new Resp.Builder<Feature>().setData(feature).ok();
//    }
//    private Integer analysisStatus(List&lt;Sprint> sprints) {
//        Date date = new Date();
//        if (sprints == null) {
//                || sprints.size() <= 0) {
//            return 0;
//        }
//        Date beginDate = null;
//        Date endDate = null;
//        for (Sprint sprint : sprints) {
//            //获取最早时间
//            if (sprint.getStartDate() == null) {
//                continue;
//            }
//            if (beginDate == null) {
//                beginDate = sprint.getStartDate();
//            } else {
//                if (DateUtil.compareDate(beginDate, sprint.getStartDate() {
//                    beginDate = sprint.getStartDate();
//                }
//            }
//            //获取最近时间
//            if (sprint.getEndDate() == null) {
//                continue;
//            }
//            if (endDate == null) {
//                endDate = sprint.getEndDate();
//            } else {
//                if (!DateUtil.compareDate(endDate, sprint.getEndDate() {
//                    endDate = sprint.getEndDate();
//                }
//            }
//        }
//        if (beginDate == null) {
//                || endDate == null
//                || !DateUtil.compareDate(endDate, date) {
//            //关闭状态：结束日期< 当前日期
//            return 0;
//        }
//        //计划中状态：当前日期大于起始日期，小于结束日期
//        if (DateUtil.compareDate(date, beginDate)
//                && !DateUtil.compareDate(date, endDate) {
//            return 2;
//        }
//        //1 开发中状态：起始日期=当前日期
//        if (DateUtil.comparisonEqualDate(date, beginDate) {
//            return 1;
//        }
//        return 0;
//    }
//    @Override
//    public Resp<List&lt;Feature>> queryList(FeatureDto feature) {
//        feature.queryListVerify();
//        String masterId = jwtUserService.getMasterId();
//        feature.setUserId(masterId);
//        feature.setFilter(queryFilterService.mysqlFilterProcess(feature.getViewTreeDto(), masterId);
//        List&lt;Feature> select = featureDao.queryList(feature);
//        select.forEach(this::accept);
//        return new Resp.Builder<List&lt;Feature>>().setData(select).total(select).ok();
//    }
//    private void accept(Feature feature) {
//        List&lt;Sprint> sprints = queryBindSprintList(feature.getId();
//        feature.setSprints(sprints);
//        feature.setStatus(analysisStatus(sprints);
//    }
//    /**
//     * update customField
//     *
//     * @Param: [feature]
//     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
//     * @Author: MaSiyi
//     * @Date: 2021/12/27
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class);
//    public Resp<String> insert(FeatureDto feature) {
//        try {
////            sysPermissionService.featurePermission(OneConstant.PERMISSION.ADD, OneConstant.SCOPE.ONE_FEATURE);
//            //验证参数
//            feature.verify();
//            //验证是否存在
////            verifyIsExist(feature.getTitle(), feature.getProjectId();
//            feature.setUserId(jwtUserService.getMasterId();
//            feature.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName();
//            Date date = new Date();
//            feature.setCreateTime(date);
//            feature.setUpdateTime(date);
//            updateFeatureJoinSprint(feature);
////            int insertFlag = featureDao.insert(feature);
//            JSONArray sysCustomField = feature.getSysCustomField();
//            if (sysCustomField != null) {
//                for (Object oField : sysCustomField) {
//                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(oField);
//                    String fieldName = jsonObject.getString("fieldName");
//                    Class<? extends FeatureDto> aClass = feature.getClass();
//                    try {
//                        Method method = aClass.getMethod("set" + StrUtil.upperFirst(OneClickUtil.lineToHump(fieldName), String.class);
//                        method.invoke(feature, jsonObject.getString("value");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//            }
////            if (insertFlag > 0) {
////                List&lt;CustomFieldData> customFieldDatas = feature.getCustomFieldDatas();
////                insertFlag = customFieldDataService.insertFeatureCustomData(customFieldDatas, feature);
////            }
//            return new Resp.Builder<String>().ok();
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#insert,error []" + e.getMessage();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
//        }
//    }
//    @Override
//    @Transactional(rollbackFor = Exception.class);
//    public Resp<String> update(Feature feature) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
//            //验证是否存在
//            verifyIsExist(feature.getTitle(), feature.getProjectId();
//            feature.setUserId(jwtUserService.getMasterId();
//            updateFeatureJoinSprint(feature);
//            return Result.updateResult(featureDao.update(feature);
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#update,error []" + e.getMessage();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
//        }
//    }
//    /**
//     * 更新关联的迭代表
//     *
//     * @param feature
//     */
//    private void updateFeatureJoinSprint(Feature feature) {
//        featureJoinSprintDao.deleteByFeatureId(feature.getId();
//        if (feature.getSprints() == null || feature.getSprints().size() <= 0) {
//            return;
//        }
//        List&lt;Sprint> sprints = feature.getSprints();
//        List&lt;FeatureJoinSprint> featureJoinSprints = new ArrayList&lt;>(sprints.size();
//        Set<String> strings = new HashSet<>(sprints.size();
//        for (Sprint sprint : sprints) {
//            if (strings.contains(sprint.getId() {
//                continue;
//            }
//            strings.add(sprint.getId();
//        }
//        for (String string : strings) {
//            FeatureJoinSprint featureJoinSprint = new FeatureJoinSprint();
//            featureJoinSprint.setSprint(string);
//            featureJoinSprint.setFeatureId(feature.getId();
//            featureJoinSprints.add(featureJoinSprint);
//        }
//        Result.addResult(featureJoinSprintDao.inserts(featureJoinSprints);
//    }
//    @Override
//    @Transactional(rollbackFor = Exception.class);
//    public Resp<String> closeUpdate(String id) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
//            Feature feature = new Feature();
//            feature.setId(id);
//            feature.setCloseDate(new Date();
//            feature.setStatus(0);
//            return Result.updateResult(featureDao.update(feature);
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#closeUpdate,error []" + e.getMessage();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
//        }
//    }
//    @Override
//    @Transactional(rollbackFor = Exception.class);
//    public Resp<String> delete(String id) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.DELETE, OneConstant.SCOPE.ONE_FEATURE);
//            Feature feature = new Feature();
//            feature.setId(id);
//            return Result.deleteResult(featureDao.deleteById(feature);
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#delete,error []" + e.getMessage();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
//        }
//    }
//    @Override
//    public Resp<List&lt;Sprint>> queryBindSprints(String featureId) {
//        List&lt;Sprint> sprints = queryBindSprintList(featureId);
//        return new Resp.Builder<List&lt;Sprint>>().setData(sprints).total(sprints.size().ok();
//    }
//    private List&lt;Sprint> queryBindSprintList(String featureId) {
//        return featureJoinSprintDao.queryBindSprints(featureId);
//    }
//    @Override
//    @Transactional(rollbackFor = Exception.class);
//    public Resp<String> bindSprintInsert(FeatureJoinSprint featureJoinSprint) {
//        try {
//            //验证参数
//            featureJoinSprint.verify();
//            //验证是否存在
//            if (featureJoinSprintDao.verifyIsExist(featureJoinSprint) > 0) {
//                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), SysConstantEnum.DATE_EXIST.getValue();
//            }
//            return Result.addResult(featureJoinSprintDao.insert(featureJoinSprint);
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#bindSprintInsert,error []" + e.getMessage();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
//        }
//    }
//    @Override
//    @Transactional(rollbackFor = Exception.class);
//    public Resp<String> bindSprintDelete(String sprint, String featureId) {
//        return Result.deleteResult(featureJoinSprintDao.deleteById(featureId, sprint);
//    }
//    @Override
//    public Resp<List&lt;Sprint>> querySprintList(String title) {
//        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
//        List&lt;Sprint> selects = sprintDao.querySprintList(title, projectId);
//        return new Resp.Builder<List&lt;Sprint>>().setData(selects).totalSize(selects.size().ok();
//    }
//    /**
//     * 查重
//     */
//    private void verifyIsExist(String title, String projectId) {
//        if (StringUtils.isEmpty(title) {
//            return;
//        }
//        Feature feature = new Feature();
//        feature.setTitle(title);
//        feature.setProjectId(projectId);
//        feature.setId(null);
//        if (featureDao.selectOne(new LambdaQueryWrapper<Feature>().eq(Feature::getTitle, feature.getTitle().eq(Feature::getProjectId, feature.getProjectId() != null) {
//            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), feature.getTitle() + SysConstantEnum.DATE_EXIST.getValue();
//        }
//    }
//    @Override
//    public List&lt;Feature> findAllByFeature(Feature feature) {
//        return featureDao.findAllByFeature(feature);
//    }
    @Override
    public PageInfo<Feature> listWithViewFilter(FeatureParam param, int pageNum, int pageSize) {
        // 检查是否需要应用视图过滤
        if (viewFilterService.shouldApplyViewFilter(param.getViewId() {
            // 使用视图过滤进行查询
            return listWithViewFilterLogic(param, pageNum, pageSize);
        } else {
            // 使用原有的简单查询逻辑
            return list(param, pageNum, pageSize);
        }
    }
    @Override
    public PageInfo<Feature> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize) {
        try {
            // 获取视图过滤参数
            Map&lt;String, Object> filterParams = viewFilterService.getFilterParamsByViewId(viewId, projectId);
            if (filterParams == null) {
                // 如果没有过滤条件，返回空分页结果
                return new PageInfo<>(new ArrayList&lt;>();
            }
            // 使用BeanSearcher进行查询，使用feature作为查询类
            Class<?> featureClass = Class.forName("com.hu.oneclick.model.entity.Feature");
            // 使用与 BeanSearchController 完全相同的逻辑：searchAll + manualPaging
            List&lt;Map&lt;String, Object>> result = mapSearcher.searchAll(featureClass, filterParams);
            // 转换为 Feature 对象
            List&lt;Feature> featureList = result.stream()
                .map(map -> BeanUtil.toBeanIgnoreError(map, Feature.class)
                .collect(Collectors.toList();
            // 使用与 BeanSearchController 相同的分页处理方式
            return PageUtil.manualPaging(featureList);
        } catch (Exception e) {
            logger.error("使用BeanSearcher查询故事失败，viewId: {}, projectId: {}", viewId, projectId, e);
            return new PageInfo<>(new ArrayList&lt;>();
        }
    }
    @Override
    public PageInfo<Feature> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize) {
        // 1. 确定表名
        String tableName = null;
        switch (scopeName) {
            case "故事": tableName = "feature"; break;
            case "测试用例": tableName = "test_case"; break;
            case "缺陷": tableName = "issue"; break;
            case "测试周期": tableName = "test_cycle"; break;
            default: tableName = "feature";
        }
        // 2. 获取 projectId
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        // 3. 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        // 添加调试日志
        logger.info("queryByFieldAndValue - 分页参数: pageNum={}, pageSize={}, offset={}", pageNum, pageSize, offset);
        logger.info("queryByFieldAndValue - 查询参数: tableName={}, fieldNameEn={}, value={}, projectId={}", tableName, fieldNameEn, value, projectId);
        // 4. 使用 DAO 方法查询数据
        List&lt;Map&lt;String, Object>> result = viewDao.queryRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null, // 不排除任何用户创建的记录
            offset,
            pageSize
        );
        logger.info("queryByFieldAndValue - 查询结果数量: {}", result.size();
        if (!result.isEmpty() {
            logger.info("queryByFieldAndValue - 第一条记录: {}", result.get(0);
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
        List&lt;Feature> featureList = result.stream().map(map -> BeanUtil.toBeanIgnoreError(map, Feature.class).collect(Collectors.toList();
        // 7. 构造 PageInfo
        PageInfo<Feature> pageInfo = new PageInfo<>(featureList);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) ((total + pageSize - 1) / pageSize);
        pageInfo.setIsFirstPage(pageNum == 1);
        pageInfo.setIsLastPage(pageNum >= pageInfo.getPages();
        pageInfo.setHasPreviousPage(pageNum > 1);
        pageInfo.setHasNextPage(pageNum < pageInfo.getPages();
        logger.info("queryByFieldAndValue - 分页信息: pageNum={}, pageSize={}, total={}, pages={}, hasNextPage={}", 
                 pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.isHasNextPage();
        return pageInfo;
    }
    /**
     * 使用视图过滤的查询逻辑，支持分页
     */
    private PageInfo<Feature> listWithViewFilterLogic(FeatureParam param, int pageNum, int pageSize) {
        try {
            // 获取视图过滤参数
            Map&lt;String, Object> filterParams = viewFilterService.getFilterParamsByViewId(
                param.getViewId(), param.getProjectId().toString();
            if (filterParams == null) {
                // 如果获取过滤参数失败，回退到简单查询
                logger.warn("获取视图过滤参数失败，回退到简单查询");
                return list(param, pageNum, pageSize);
            }
            // 使用BeanSearcher进行查询
            Class<?> featureClass = Class.forName("com.hu.oneclick.model.entity.Feature");
            List&lt;Map&lt;String, Object>> result = mapSearcher.searchAll(featureClass, filterParams);
            // 转换为 Feature 对象
            List&lt;Feature> featureList = result.stream()
                .map(map -> BeanUtil.toBeanIgnoreError(map, Feature.class)
                .collect(Collectors.toList();
            // 手动分页处理
            int total = featureList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            List&lt;Feature> pageData = new ArrayList&lt;>();
            if (startIndex < total) {
                pageData = featureList.subList(startIndex, endIndex);
            }
            PageInfo<Feature> pageInfo = new PageInfo<>(pageData);
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
    private PageInfo<Feature> list(FeatureParam param, int pageNum, int pageSize) {
        // 手动设置分页参数
        PageUtil.startPage(pageNum, pageSize);
        List&lt;Feature> dataList = this.list(param);
        return PageInfo.of(dataList);
    }
}
}
}
