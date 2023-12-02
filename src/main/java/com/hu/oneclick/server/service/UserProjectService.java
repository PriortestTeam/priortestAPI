package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;

import java.util.List;

public interface UserProjectService {
    Resp<List<LeftJoinDto>> getUserByProject();
}
