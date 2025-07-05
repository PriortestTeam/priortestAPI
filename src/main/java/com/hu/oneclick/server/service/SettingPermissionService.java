package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface SettingPermissionService {

    Resp<SubUserPermissionDto> getPermissions(String subUserId,String projectId);

    Resp<String> updatePermissions(SubUserPermissionDto entity);

    Resp<List<Project>> getProjects(String subUserId);
}
