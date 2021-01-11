package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysOperationAuthority;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface SettingPermissionService {

    Resp<List<SysOperationAuthority>> getSysOperationAuthority();

    Resp<SubUserPermissionDto> getPermissions(String subUserId);

    Resp<String> updatePermissions(SubUserPermissionDto entity);
}
