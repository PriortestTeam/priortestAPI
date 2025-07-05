package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;

import java.util.List;
import java.util.Map;

public interface UserProjectService {
    Resp<List<LeftJoinDto>> getUserByProject();

    List<Map<String, Object>> getUserProject();
}
