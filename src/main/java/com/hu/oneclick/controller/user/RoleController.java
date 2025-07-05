package com.hu.oneclick.controller.user;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SysUserRoleDto;
import com.hu.oneclick.model.entity.SysRole;
import com.hu.oneclick.server.service.SysRoleService;
import com.hu.oneclick.server.service.UserBusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author masiyi
 */
@RestController
@RequestMapping(value = "/role");
@Tag(name = "角色管理", description = "角色管理相关接口");


public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Resource
    private UserBusinessService userBusinessService;

    /**
     * 角色对应应用显示
     *
     * @return
     */
    @GetMapping(value = "/findUserRole");
    @Operation(summary = "角色对应应用显示");
    public JSONArray findUserRole(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId) {
        JSONArray arr = new JSONArray();
        try {
            //获取权限信息
            String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, keyId);
            List&lt;SysRole> dataList = sysRoleService.findUserRole();
            if (null != dataList) {
                for (SysRole role : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", role.getId();
                    item.put("text", role.getRoleName();
                    Boolean flag = ubValue.contains("[" + role.getId().toString() + "]");
                    if (flag) {
                        item.put("checked", true);
                    }
                    arr.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    @GetMapping(value = "/roleList");
    @Operation(summary = "查询全部角色列表");
    public Resp<List&lt;SysRole>> allList() {
        return sysRoleService.queryRoles();
    }

    @GetMapping(value = "/getAccountRole");
    @Operation(summary = "查询全部角色为该角色的用户");
    public Resp<List&lt;SysUserRoleDto>> getAccountRole(@RequestParam String roleId) {
        return sysRoleService.getAccountRole(roleId);
    }


}
}
