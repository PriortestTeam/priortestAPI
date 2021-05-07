package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.server.service.SprintService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SprintServiceImpl implements SprintService {

    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);

    private final SprintDao sprintDao;

    private final JwtUserServiceImpl jwtUserService;

    private final SysPermissionService sysPermissionService;

    public SprintServiceImpl(SprintDao sprintDao, JwtUserServiceImpl jwtUserService, SysPermissionService sysPermissionService) {
        this.sprintDao = sprintDao;
        this.jwtUserService = jwtUserService;
        this.sysPermissionService = sysPermissionService;
    }

    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = sprintDao.queryTitles(projectId,title,jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select.size()).ok();
    }


    @Override
    public Resp<Sprint> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        Sprint sprint =  sprintDao.queryById(id,masterId);
        return new Resp.Builder<Sprint>().setData(sprint).ok();
    }

    @Override
    public Resp<List<Sprint>> queryList(Sprint sprint) {
        sprint.queryListVerify();
        sprint.setUserId(jwtUserService.getMasterId());
        List<Sprint> select = sprintDao.queryList(sprint);
        select.forEach(this::analysisStatus);
        return new Resp.Builder<List<Sprint>>().setData(select).total(select).ok();
    }

    public void analysisStatus(Sprint sprint){
        Date date = new Date();
        if (sprint.getStartDate() == null
                || sprint.getEndDate() == null
                || !DateUtil.compareDate(sprint.getEndDate(),date)){
            //关闭状态：结束日期< 当前日期
            sprint.setStatus(0);
            return;
        }
        //计划中状态：当前日期大于起始日期，小于结束日期
        if (DateUtil.compareDate(date,sprint.getStartDate())
                && !DateUtil.compareDate(date,sprint.getEndDate())){
            sprint.setStatus(2);
            return;
        }
        //1 开发中状态：起始日期=当前日期
        if (DateUtil.comparisonEqualDate(date,sprint.getStartDate())){
            sprint.setStatus(1);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(Sprint sprint) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.ADD,OneConstant.SCOPE.ONE_SPRINT);
            //验证参数
            sprint.verify();
            //验证是否存在
            verifyIsExist(sprint.getTitle(),sprint.getProjectId());
            sprint.setUserId(jwtUserService.getMasterId());
            Date date = new Date();
            sprint.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            sprint.setCreateTime(date);
            sprint.setUpdateTime(date);
            return Result.addResult(sprintDao.insert(sprint));
        }catch (BizException e){
            logger.error("class: SprintServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(Sprint sprint) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.EDIT,OneConstant.SCOPE.ONE_SPRINT);
            //验证是否存在
            verifyIsExist(sprint.getTitle(),sprint.getProjectId());
            sprint.setUserId(jwtUserService.getMasterId());
            return Result.updateResult(sprintDao.update(sprint));
        }catch (BizException e){
            logger.error("class: SprintServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            sysPermissionService.featurePermission(OneConstant.PERMISSION.DELETE,OneConstant.SCOPE.ONE_SPRINT);
            Sprint sprint = new Sprint();
            sprint.setId(id);
            return Result.deleteResult(sprintDao.delete(sprint));
        }catch (BizException e){
            logger.error("class: SprintServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }


    /**
     *  查重
     */
    private void verifyIsExist(String title,String projectId){
        if (StringUtils.isEmpty(title)){
            return;
        }
        Sprint sprint = new Sprint();
        sprint.setTitle(title);
        sprint.setProjectId(projectId);
        sprint.setId(null);
        if (sprintDao.selectOne(sprint) != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),sprint.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }
}
