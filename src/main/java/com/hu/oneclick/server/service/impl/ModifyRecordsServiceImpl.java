package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ModifyRecordDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.entity.ModifyRecord;
import com.hu.oneclick.server.service.ModifyRecordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModifyRecordsServiceImpl implements ModifyRecordsService {

    private final static Logger logger = LoggerFactory.getLogger(ModifyRecordsServiceImpl.class);

    private final ModifyRecordDao modifyRecordDao;

    private final JwtUserServiceImpl jwtUserService;

    public ModifyRecordsServiceImpl(ModifyRecordDao modifyRecordDao, JwtUserServiceImpl jwtUserService) {
        this.modifyRecordDao = modifyRecordDao;
        this.jwtUserService = jwtUserService;
    }

    @Override
    public Resp<List<ModifyRecord>> queryList(ModifyRecord modifyRecord) {
        try {
            modifyRecord.verify();
            modifyRecord.setUserId(jwtUserService.getMasterId();
            List<ModifyRecord> select = modifyRecordDao.queryList(modifyRecord);
            return new Resp.Builder<List<ModifyRecord>>().setData(select).total(select.size().ok();
        } catch (BizException e) {
            logger.error("class: ModifyRecordServiceImpl#queryList,error []" + e.getMessage();
            return new Resp.Builder<List<ModifyRecord>>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class);
    public void insert(List<ModifyRecord> modifyRecord) {
        Result.addResult(modifyRecordDao.inserts(modifyRecord);
    }

}
