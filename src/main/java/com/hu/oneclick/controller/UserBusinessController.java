package com.hu.oneclick.controller;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.UserBusinessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author masiyi
 */
@RestController
@RequestMapping(value = "/userBusiness")
@Api(tags = "用户角色权限模块")
public class UserBusinessController {

    @Autowired
    private UserBusinessService userBusinessService;
    /**
     * 校验存在
     *
     * @param type
     * @param keyId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkIsValueExist")
    @ApiOperation(value = "校验存在")
    public Resp<Long> checkIsValueExist(@RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "keyId", required = false) String keyId) {
        Long id = userBusinessService.checkIsValueExist(type, keyId);
        return new Resp.Builder<Long>().setData(id).ok();
    }

    /**
     * 更新角色的按钮权限
     *
     * @param jsonObject
     * @return
     */
    @PostMapping(value = "/updateBtnStr")
    @ApiOperation(value = "更新角色的按钮权限")
    public Resp<String> updateBtnStr(@RequestBody JSONObject jsonObject) {

        String roleId = jsonObject.getString("roleId");
        String btnStr = jsonObject.getString("btnStr");
        String keyId = roleId;
        String type = "RoleFunctions";
        Integer integer = userBusinessService.updateBtnStr(keyId, type, btnStr);
        if (integer > 0) {

            return new Resp.Builder<String>().ok();
        } else {
            return new Resp.Builder<String>().fail();
        }
    }
}
