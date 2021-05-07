package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.FeatureJoinSprintDao;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.FeatureJoinSprint;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.server.service.FeatureService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * @author qingyang
 */
@Service
public class FeatureServiceImpl implements FeatureService {

    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);


    private final FeatureDao featureDao;

    private final FeatureJoinSprintDao featureJoinSprintDao;

    private final SprintDao sprintDao;

    private final JwtUserServiceImpl jwtUserService;

    private final SysPermissionService sysPermissionService;

    public FeatureServiceImpl(FeatureDao featureDao, FeatureJoinSprintDao featureJoinSprintDao, SprintDao sprintDao, JwtUserServiceImpl jwtUserService, SysPermissionService sysPermissionService) {
        this.featureDao = featureDao;
        this.featureJoinSprintDao = featureJoinSprintDao;
        this.sprintDao = sprintDao;
        this.jwtUserService = jwtUserService;
        this.sysPermissionService = sysPermissionService;
    }


    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = featureDao.queryTitles(projectId, title, jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select.size()).ok();
    }


    @Override
    public Resp<Feature> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        Feature feature = featureDao.queryById(id, masterId);
        List<Sprint> sprints = queryBindSprintList(id);
        feature.setSprints(sprints);
        feature.setStatus(analysisStatus(sprints));
        return new Resp.Builder<Feature>().setData(feature).ok();
    }

    private Integer analysisStatus(List<Sprint> sprints) {
        Date date = new Date();
        if (sprints == null
                || sprints.size() <= 0) {
            return 0;
        }
        Date beginDate = null;
        Date endDate = null;
        for (Sprint sprint : sprints) {
            //获取最早时间
            if (sprint.getStartDate() == null) {
                continue;
            }
            if (beginDate == null) {
                beginDate = sprint.getStartDate();
            } else {
                if (DateUtil.compareDate(beginDate, sprint.getStartDate())) {
                    beginDate = sprint.getStartDate();
                }
            }

            //获取最近时间
            if (sprint.getEndDate() == null) {
                continue;
            }
            if (endDate == null) {
                endDate = sprint.getEndDate();
            } else {
                if (!DateUtil.compareDate(endDate, sprint.getEndDate())) {
                    endDate = sprint.getEndDate();
                }
            }
        }

        if (beginDate == null
                || endDate == null
                || !DateUtil.compareDate(endDate,date)){
            //关闭状态：结束日期< 当前日期
            return 0;
        }
        //计划中状态：当前日期大于起始日期，小于结束日期
        if (DateUtil.compareDate(date,beginDate)
                && !DateUtil.compareDate(date,endDate)){
            return 2;
        }
        //1 开发中状态：起始日期=当前日期
        if (DateUtil.comparisonEqualDate(date,beginDate)){
            return 1;
        }
        return 0;
    }

    @Override
    public Resp<List<Feature>> queryList(Feature feature) {
        feature.queryListVerify();
        feature.setUserId(jwtUserService.getMasterId());
        List<Feature> select = featureDao.queryList(feature);
        select.forEach(this::accept);
        return new Resp.Builder<List<Feature>>().setData(select).total(select).ok();
    }
    private void accept(Feature feature) {
        List<Sprint> sprints = queryBindSprintList(feature.getId());
        feature.setSprints(sprints);
        feature.setStatus(analysisStatus(sprints));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(Feature feature) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.ADD, OneConstant.SCOPE.ONE_FEATURE);
            //验证参数
            feature.verify();
            //验证是否存在
            verifyIsExist(feature.getTitle(), feature.getProjectId());
            feature.setUserId(jwtUserService.getMasterId());
            feature.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            Date date = new Date();
            feature.setCreateTime(date);
            feature.setUpdateTime(date);
            updateFeatureJoinSprint(feature);
            return Result.addResult(featureDao.insert(feature));
        } catch (BizException e) {
            logger.error("class: FeatureServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(Feature feature) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
            //验证是否存在
            verifyIsExist(feature.getTitle(), feature.getProjectId());
            feature.setUserId(jwtUserService.getMasterId());
            updateFeatureJoinSprint(feature);
            return Result.updateResult(featureDao.update(feature));
        } catch (BizException e) {
            logger.error("class: FeatureServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 更新关联的迭代表
     * @param feature
     */
    private void updateFeatureJoinSprint(Feature feature){
        featureJoinSprintDao.deleteByFeatureId(feature.getId());

        if (feature.getSprints() == null || feature.getSprints().size() <= 0){
            return;
        }

        List<Sprint> sprints = feature.getSprints();
        List<FeatureJoinSprint> featureJoinSprints = new ArrayList<>(sprints.size());

        Set<String> strings = new HashSet<>(sprints.size());
        for (Sprint sprint : sprints) {
            if (strings.contains(sprint.getId())){
                continue;
            }
            strings.add(sprint.getId());
        }
        for (String string : strings) {
            FeatureJoinSprint featureJoinSprint = new FeatureJoinSprint();
            featureJoinSprint.setSprint(string);
            featureJoinSprint.setFeatureId(feature.getId());
            featureJoinSprints.add(featureJoinSprint);
        }
        Result.addResult(featureJoinSprintDao.inserts(featureJoinSprints));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> closeUpdate(String id) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT, OneConstant.SCOPE.ONE_FEATURE);
            Feature feature = new Feature();
            feature.setId(id);
            feature.setCloseDate(new Date());
            feature.setStatus(0);
            return Result.updateResult(featureDao.update(feature));
        } catch (BizException e) {
            logger.error("class: FeatureServiceImpl#closeUpdate,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.DELETE, OneConstant.SCOPE.ONE_FEATURE);
            Feature feature = new Feature();
            feature.setId(id);
            return Result.deleteResult(featureDao.delete(feature));
        } catch (BizException e) {
            logger.error("class: FeatureServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<List<Sprint>> queryBindSprints(String featureId) {
        List<Sprint> sprints = queryBindSprintList(featureId);
        return new Resp.Builder<List<Sprint>>().setData(sprints).total(sprints.size()).ok();
    }

    private List<Sprint> queryBindSprintList(String featureId) {
        return featureJoinSprintDao.queryBindSprints(featureId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> bindSprintInsert(FeatureJoinSprint featureJoinSprint) {
        try {
            //验证参数
            featureJoinSprint.verify();
            //验证是否存在
            if (featureJoinSprintDao.verifyIsExist(featureJoinSprint) > 0) {
                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), SysConstantEnum.DATE_EXIST.getValue());
            }
            return Result.addResult(featureJoinSprintDao.insert(featureJoinSprint));
        } catch (BizException e) {
            logger.error("class: FeatureServiceImpl#bindSprintInsert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> bindSprintDelete(String sprint, String featureId) {
        return Result.deleteResult(featureJoinSprintDao.deleteById(featureId, sprint));
    }

    @Override
    public Resp<List<Sprint>> querySprintList(String title) {
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        List<Sprint> selects = sprintDao.querySprintList(title,projectId);
        return new Resp.Builder<List<Sprint>>().setData(selects).total(selects.size()).ok();
    }

    /**
     * 查重
     */
    private void verifyIsExist(String title, String projectId) {
        if (StringUtils.isEmpty(title)) {
            return;
        }
        Feature feature = new Feature();
        feature.setTitle(title);
        feature.setProjectId(projectId);
        feature.setId(null);
        if (featureDao.selectOne(feature) != null) {
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), feature.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }
}
