package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;

/**
 * @author qingyang
 */
public interface SettingPermissionService {
    Resp<SubUserPermissionDto> getPermissions(String subUserId);

    Resp<String> updatePermissions(SubUserPermissionDto entity);
}
