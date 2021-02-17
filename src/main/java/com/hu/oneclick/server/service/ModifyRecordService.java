package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.ModifyRecord;

import java.util.List;

public interface ModifyRecordService {
    Resp<List<ModifyRecord>> queryList(ModifyRecord modifyRecord);

    void insert(List<ModifyRecord> modifyRecord);
}
