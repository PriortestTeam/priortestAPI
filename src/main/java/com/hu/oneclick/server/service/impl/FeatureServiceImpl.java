package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.FeatureJoinSprintDao;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.param.FeatureParam;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.FeatureService;
import com.hu.oneclick.server.service.QueryFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qingyang
 */
@Service
public class FeatureServiceImpl extends ServiceImpl<FeatureDao, Feature> implements FeatureService {

    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);

    @Resource
    private FeatureDao featureDao;
    @Resource
    private FeatureJoinSprintDao featureJoinSprintDao;
    @Resource
    private SprintDao sprintDao;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private QueryFilterService queryFilterService;
    @Resource
    private CustomFieldDataService customFieldDataService;

    @Override
    public List<Feature> list(FeatureParam param) {
        return this.list(param.getQueryCondition());
    }

    @Override
    public Feature add(FeatureSaveDto dto) {
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.insert(feature);
        return feature;
    }

    @Override
    public Feature edit(FeatureSaveDto dto) {
        Feature entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("故事查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
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
            throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id));
        }
        return feature;
    }

    @Override
    public void clone(List<Long> ids) {
        List<Feature> featureList = new ArrayList<>();
        for (Long id : ids) {
            Feature feature = baseMapper.selectById(id);
            if (feature == null) {
                throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id));
            }
            Feature issueClone = new Feature();
            BeanUtil.copyProperties(feature, issueClone);
            issueClone.setId(null);
            issueClone.setTitle(CloneFormatUtil.getCloneTitle(feature.getTitle()));
            featureList.add(issueClone);
        }
        // 批量克隆
        this.saveBatch(featureList);
    }

    @Override
    public List<Map<String, String>> getFeatureByTitle(String title, Long projectId) {
        QueryWrapper<Feature> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(Feature::getTitle, title)
                .eq(Feature::getProjectId, projectId);
        List<Feature> features = baseMapper.selectList(queryWrapper);

        return features.stream()
                .map(feature -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", feature.getId().toString());
                    map.put("title", feature.getTitle());
                    return map;
                })
                .collect(Collectors.toList());
    }

    //    @Override
//    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
//        List<LeftJoinDto> select = featureDao.queryTitles(projectId, title, jwtUserService.getMasterId());
//        return new Resp.Builder<List<LeftJoinDto>>().setData(select).totalSize(select.size()).ok();
//    }
//
//
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
//
//        //查询自定义数据
//        List<CustomFieldData> customFieldData = customFieldDataService.featureRenderingCustom(id);
//        List<Sprint> sprints = queryBindSprintList(id);
//        feature.setSprints(sprints);
//        feature.setStatus(analysisStatus(sprints));
//        feature.setCustomFieldDatas(customFieldData);
//        return new Resp.Builder<Feature>().setData(feature).ok();
//    }
//
//    private Integer analysisStatus(List<Sprint> sprints) {
//        Date date = new Date();
//        if (sprints == null
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
//                if (DateUtil.compareDate(beginDate, sprint.getStartDate())) {
//                    beginDate = sprint.getStartDate();
//                }
//            }
//
//            //获取最近时间
//            if (sprint.getEndDate() == null) {
//                continue;
//            }
//            if (endDate == null) {
//                endDate = sprint.getEndDate();
//            } else {
//                if (!DateUtil.compareDate(endDate, sprint.getEndDate())) {
//                    endDate = sprint.getEndDate();
//                }
//            }
//        }
//
//        if (beginDate == null
//                || endDate == null
//                || !DateUtil.compareDate(endDate, date)) {
//            //关闭状态：结束日期< 当前日期
//            return 0;
//        }
//        //计划中状态：当前日期大于起始日期，小于结束日期
//        if (DateUtil.compareDate(date, beginDate)
//                && !DateUtil.compareDate(date, endDate)) {
//            return 2;
//        }
//        //1 开发中状态：起始日期=当前日期
//        if (DateUtil.comparisonEqualDate(date, beginDate)) {
//            return 1;
//        }
//        return 0;
//    }
//
//    @Override
//    public Resp<List<Feature>> queryList(FeatureDto feature) {
//        feature.queryListVerify();
//        String masterId = jwtUserService.getMasterId();
//        feature.setUserId(masterId);
//
//        feature.setFilter(queryFilterService.mysqlFilterProcess(feature.getViewTreeDto(), masterId));
//
//        List<Feature> select = featureDao.queryList(feature);
//        select.forEach(this::accept);
//        return new Resp.Builder<List<Feature>>().setData(select).total(select).ok();
//    }
//
//    private void accept(Feature feature) {
//        List<Sprint> sprints = queryBindSprintList(feature.getId());
//        feature.setSprints(sprints);
//        feature.setStatus(analysisStatus(sprints));
//    }
//
//    /**
//     * update customField
//     *
//     * @Param: [feature]
//     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
//     * @Author: MaSiyi
//     * @Date: 2021/12/27
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> insert(FeatureDto feature) {
//        try {
////            sysPermissionService.featurePermission(OneConstant.PERMISSION.ADD, OneConstant.SCOPE.ONE_FEATURE);
//            //验证参数
//            feature.verify();
//            //验证是否存在
////            verifyIsExist(feature.getTitle(), feature.getProjectId());
//            feature.setUserId(jwtUserService.getMasterId());
//            feature.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
//            Date date = new Date();
//            feature.setCreateTime(date);
//            feature.setUpdateTime(date);
//            updateFeatureJoinSprint(feature);
////            int insertFlag = featureDao.insert(feature);
//            JSONArray sysCustomField = feature.getSysCustomField();
//            if (sysCustomField != null) {
//                for (Object oField : sysCustomField) {
//                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(oField));
//                    String fieldName = jsonObject.getString("fieldName");
//                    Class<? extends FeatureDto> aClass = feature.getClass();
//                    try {
//                        Method method = aClass.getMethod("set" + StrUtil.upperFirst(OneClickUtil.lineToHump(fieldName)), String.class);
//                        method.invoke(feature, jsonObject.getString("value"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//            }
//
//
////            if (insertFlag > 0) {
////                List<CustomFieldData> customFieldDatas = feature.getCustomFieldDatas();
////                insertFlag = customFieldDataService.insertFeatureCustomData(customFieldDatas, feature);
////            }
//
//            return new Resp.Builder<String>().ok();
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#insert,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> update(Feature feature) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
//            //验证是否存在
//            verifyIsExist(feature.getTitle(), feature.getProjectId());
//            feature.setUserId(jwtUserService.getMasterId());
//            updateFeatureJoinSprint(feature);
//            return Result.updateResult(featureDao.update(feature));
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#update,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    /**
//     * 更新关联的迭代表
//     *
//     * @param feature
//     */
//    private void updateFeatureJoinSprint(Feature feature) {
//        featureJoinSprintDao.deleteByFeatureId(feature.getId());
//
//        if (feature.getSprints() == null || feature.getSprints().size() <= 0) {
//            return;
//        }
//
//        List<Sprint> sprints = feature.getSprints();
//        List<FeatureJoinSprint> featureJoinSprints = new ArrayList<>(sprints.size());
//
//        Set<String> strings = new HashSet<>(sprints.size());
//        for (Sprint sprint : sprints) {
//            if (strings.contains(sprint.getId())) {
//                continue;
//            }
//            strings.add(sprint.getId());
//        }
//        for (String string : strings) {
//            FeatureJoinSprint featureJoinSprint = new FeatureJoinSprint();
//            featureJoinSprint.setSprint(string);
//            featureJoinSprint.setFeatureId(feature.getId());
//            featureJoinSprints.add(featureJoinSprint);
//        }
//        Result.addResult(featureJoinSprintDao.inserts(featureJoinSprints));
//    }
//
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> closeUpdate(String id) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
//            Feature feature = new Feature();
//            feature.setId(id);
//            feature.setCloseDate(new Date());
//            feature.setStatus(0);
//            return Result.updateResult(featureDao.update(feature));
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#closeUpdate,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> delete(String id) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.DELETE, OneConstant.SCOPE.ONE_FEATURE);
//            Feature feature = new Feature();
//            feature.setId(id);
//            return Result.deleteResult(featureDao.deleteById(feature));
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#delete,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    public Resp<List<Sprint>> queryBindSprints(String featureId) {
//        List<Sprint> sprints = queryBindSprintList(featureId);
//        return new Resp.Builder<List<Sprint>>().setData(sprints).total(sprints.size()).ok();
//    }
//
//    private List<Sprint> queryBindSprintList(String featureId) {
//        return featureJoinSprintDao.queryBindSprints(featureId);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> bindSprintInsert(FeatureJoinSprint featureJoinSprint) {
//        try {
//            //验证参数
//            featureJoinSprint.verify();
//            //验证是否存在
//            if (featureJoinSprintDao.verifyIsExist(featureJoinSprint) > 0) {
//                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), SysConstantEnum.DATE_EXIST.getValue());
//            }
//            return Result.addResult(featureJoinSprintDao.insert(featureJoinSprint));
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#bindSprintInsert,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> bindSprintDelete(String sprint, String featureId) {
//        return Result.deleteResult(featureJoinSprintDao.deleteById(featureId, sprint));
//    }
//
//    @Override
//    public Resp<List<Sprint>> querySprintList(String title) {
//        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
//        List<Sprint> selects = sprintDao.querySprintList(title, projectId);
//        return new Resp.Builder<List<Sprint>>().setData(selects).totalSize(selects.size()).ok();
//    }
//
//    /**
//     * 查重
//     */
//    private void verifyIsExist(String title, String projectId) {
//        if (StringUtils.isEmpty(title)) {
//            return;
//        }
//        Feature feature = new Feature();
//        feature.setTitle(title);
//        feature.setProjectId(projectId);
//        feature.setId(null);
//        if (featureDao.selectOne(new LambdaQueryWrapper<Feature>().eq(Feature::getTitle, feature.getTitle()).eq(Feature::getProjectId, feature.getProjectId())) != null) {
//            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), feature.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
//        }
//    }
//
//    @Override
//    public List<Feature> findAllByFeature(Feature feature) {
//        return featureDao.findAllByFeature(feature);
//    }
}
```

```java
package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.FeatureJoinSprintDao;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.param.FeatureParam;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.FeatureService;
import com.hu.oneclick.server.service.QueryFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.fastjson2.JSON;

/**
 * @author qingyang
 */
@Service
public class FeatureServiceImpl extends ServiceImpl<FeatureDao, Feature> implements FeatureService {

    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);

    @Resource
    private FeatureDao featureDao;
    @Resource
    private FeatureJoinSprintDao featureJoinSprintDao;
    @Resource
    private SprintDao sprintDao;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private QueryFilterService queryFilterService;
    @Resource
    private CustomFieldDataService customFieldDataService;

    @Override
    public List<Feature> list(FeatureParam param) {
        return this.list(param.getQueryCondition());
    }

    @Override
    public Feature add(FeatureSaveDto dto) {
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.insert(feature);
        return feature;
    }

    @Override
    public Feature edit(FeatureSaveDto dto) {
        Feature entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("故事查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
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
            throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id));
        }
        return feature;
    }

    @Override
    public void clone(List<Long> ids) {
        List<Feature> featureList = new ArrayList<>();
        for (Long id : ids) {
            Feature feature = baseMapper.selectById(id);
            if (feature == null) {
                throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id));
            }
            Feature issueClone = new Feature();
            BeanUtil.copyProperties(feature, issueClone);
            issueClone.setId(null);
            issueClone.setTitle(CloneFormatUtil.getCloneTitle(feature.getTitle()));
            featureList.add(issueClone);
        }
        // 批量克隆
        this.saveBatch(featureList);
    }

    @Override
    public List<Map<String, String>> getFeatureByTitle(String title, Long projectId) {
        QueryWrapper<Feature> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(Feature::getTitle, title)
                .eq(Feature::getProjectId, projectId);
        List<Feature> features = baseMapper.selectList(queryWrapper);

        return features.stream()
                .map(feature -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", feature.getId().toString());
                    map.put("title", feature.getTitle());
                    return map;
                })
                .collect(Collectors.toList());
    }

    //    @Override
//    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
//        List<LeftJoinDto> select = featureDao.queryTitles(projectId, title, jwtUserService.getMasterId());
//        return new Resp.Builder<List<LeftJoinDto>>().setData(select).totalSize(select.size()).ok();
//    }
//
//
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
//
//        //查询自定义数据
//        List<CustomFieldData> customFieldData = customFieldDataService.featureRenderingCustom(id);
//        List<Sprint> sprints = queryBindSprintList(id);
//        feature.setSprints(sprints);
//        feature.setStatus(analysisStatus(sprints));
//        feature.setCustomFieldDatas(customFieldData);
//        return new Resp.Builder<Feature>().setData(feature).ok();
//    }
//
//    private Integer analysisStatus(List<Sprint> sprints) {
//        Date date = new Date();
//        if (sprints == null
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
//                if (DateUtil.compareDate(beginDate, sprint.getStartDate())) {
//                    beginDate = sprint.getStartDate();
//                }
//            }
//
//            //获取最近时间
//            if (sprint.getEndDate() == null) {
//                continue;
//            }
//            if (endDate == null) {
//                endDate = sprint.getEndDate();
//            } else {
//                if (!DateUtil.compareDate(endDate, sprint.getEndDate())) {
//                    endDate = sprint.getEndDate();
//                }
//            }
//        }
//
//        if (beginDate == null
//                || endDate == null
//                || !DateUtil.compareDate(endDate, date)) {
//            //关闭状态：结束日期< 当前日期
//            return 0;
//        }
//        //计划中状态：当前日期大于起始日期，小于结束日期
//        if (DateUtil.compareDate(date, beginDate)
//                && !DateUtil.compareDate(date, endDate)) {
//            return 2;
//        }
//        //1 开发中状态：起始日期=当前日期
//        if (DateUtil.comparisonEqualDate(date, beginDate)) {
//            return 1;
//        }
//        return 0;
//    }
//
//    @Override
//    public Resp<List<Feature>> queryList(FeatureDto feature) {
//        feature.queryListVerify();
//        String masterId = jwtUserService.getMasterId();
//        feature.setUserId(masterId);
//
//        feature.setFilter(queryFilterService.mysqlFilterProcess(feature.getViewTreeDto(), masterId));
//
//        List<Feature> select = featureDao.queryList(feature);
//        select.forEach(this::accept);
//        return new Resp.Builder<List<Feature>>().setData(select).total(select).ok();
//    }
//
//    private void accept(Feature feature) {
//        List<Sprint> sprints = queryBindSprintList(feature.getId());
//        feature.setSprints(sprints);
//        feature.setStatus(analysisStatus(sprints));
//    }
//
//    /**
//     * update customField
//     *
//     * @Param: [feature]
//     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
//     * @Author: MaSiyi
//     * @Date: 2021/12/27
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> insert(FeatureDto feature) {
//        try {
////            sysPermissionService.featurePermission(OneConstant.PERMISSION.ADD, OneConstant.SCOPE.ONE_FEATURE);
//            //验证参数
//            feature.verify();
//            //验证是否存在
////            verifyIsExist(feature.getTitle(), feature.getProjectId());
//            feature.setUserId(jwtUserService.getMasterId());
//            feature.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
//            Date date = new Date();
//            feature.setCreateTime(date);
//            feature.setUpdateTime(date);
//            updateFeatureJoinSprint(feature);
////            int insertFlag = featureDao.insert(feature);
//            JSONArray sysCustomField = feature.getSysCustomField();
//            if (sysCustomField != null) {
//                for (Object oField : sysCustomField) {
//                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(oField));
//                    String fieldName = jsonObject.getString("fieldName");
//                    Class<? extends FeatureDto> aClass = feature.getClass();
//                    try {
//                        Method method = aClass.getMethod("set" + StrUtil.upperFirst(OneClickUtil.lineToHump(fieldName)), String.class);
//                        method.invoke(feature, jsonObject.getString("value"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//            }
//
//
////            if (insertFlag > 0) {
////                List<CustomFieldData> customFieldDatas = feature.getCustomFieldDatas();
////                insertFlag = customFieldDataService.insertFeatureCustomData(customFieldDatas, feature);
////            }
//
//            return new Resp.Builder<String>().ok();
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#insert,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> update(Feature feature) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
//            //验证是否存在
//            verifyIsExist(feature.getTitle(), feature.getProjectId());
//            feature.setUserId(jwtUserService.getMasterId());
//            updateFeatureJoinSprint(feature);
//            return Result.updateResult(featureDao.update(feature));
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#update,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    /**
//     * 更新关联的迭代表
//     *
//     * @param feature
//     */
//    private void updateFeatureJoinSprint(Feature feature) {
//        featureJoinSprintDao.deleteByFeatureId(feature.getId());
//
//        if (feature.getSprints() == null || feature.getSprints().size() <= 0) {
//            return;
//        }
//
//        List<Sprint> sprints = feature.getSprints();
//        List<FeatureJoinSprint> featureJoinSprints = new ArrayList<>(sprints.size());
//
//        Set<String> strings = new HashSet<>(sprints.size());
//        for (Sprint sprint : sprints) {
//            if (strings.contains(sprint.getId())) {
//                continue;
//            }
//            strings.add(sprint.getId());
//        }
//        for (String string : strings) {
//            FeatureJoinSprint featureJoinSprint = new FeatureJoinSprint();
//            featureJoinSprint.setSprint(string);
//            featureJoinSprint.setFeatureId(feature.getId());
//            featureJoinSprints.add(featureJoinSprint);
//        }
//        Result.addResult(featureJoinSprintDao.inserts(featureJoinSprints));
//    }
//
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> closeUpdate(String id) {
//        try {
//            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
//            Feature feature = new Feature();
//            feature.setId(id);
//            feature.setCloseDate(new Date());
//            feature.setStatus(0);
//            return Result.updateResult(featureDao.update(feature));
//        } catch (BizException e) {
//            logger.error("class: FeatureServiceImpl#closeUpdate,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return