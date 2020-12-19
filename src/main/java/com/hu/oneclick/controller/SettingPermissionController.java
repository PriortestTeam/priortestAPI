package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;
import com.hu.oneclick.server.service.SettingPermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qingyang
 */
@PreAuthorize("@ps.manageSubUsers()")
@RestController
@RequestMapping("settingPermission")
public class SettingPermissionController {

    private final SettingPermissionService settingPermissionService;

    public SettingPermissionController(SettingPermissionService settingPermissionService) {
        this.settingPermissionService = settingPermissionService;
    }

    @GetMapping("getPermissions/{subUserId}")
    public Resp<SubUserPermissionDto> getPermissions(@PathVariable String subUserId){
        return settingPermissionService.getPermissions(subUserId);
    }

}
