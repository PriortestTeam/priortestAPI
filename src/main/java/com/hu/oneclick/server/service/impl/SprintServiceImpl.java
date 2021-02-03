package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.server.service.SprintService;
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

    public SprintServiceImpl(SprintDao sprintDao, JwtUserServiceImpl jwtUserService, JwtUserServiceImpl jwtUserService1) {
        this.sprintDao = sprintDao;
        this.jwtUserService = jwtUserService1;
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
        List<Sprint> select = sprintDao.select(sprint);
        return new Resp.Builder<List<Sprint>>().setData(select).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(Sprint sprint) {
        try {
            sprint.verify();
            sprint.setUserId(jwtUserService.getMasterId());
            Date date = new Date();
            sprint.setCreateTime(date);
            sprint.setUpdateTime(date);
            return Result.addResult(sprintDao.insert(sprint));
        }catch (BaseException e){
            logger.error("class: SprintServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(Sprint sprint) {
        try {
            sprint.verify();
            sprint.setUserId(jwtUserService.getMasterId());
            sprint.setUpdateTime(new Date());
            return Result.updateResult(sprintDao.update(sprint));
        }catch (BaseException e){
            logger.error("class: SprintServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            Sprint sprint = new Sprint();
            sprint.setId(id);
            return Result.deleteResult(sprintDao.delete(sprint));
        }catch (BaseException e){
            logger.error("class: SprintServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }
}
