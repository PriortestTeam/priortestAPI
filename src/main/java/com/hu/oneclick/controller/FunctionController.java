package com.hu.oneclick.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysFunction;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.entity.SysUserBusiness;
import com.hu.oneclick.model.domain.dto.RoleProjectFunctionDTO;
import com.hu.oneclick.server.service.FunctionService;
import com.hu.oneclick.server.service.UserBusinessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;

/**
 * @author masiyi
 */
@Slf4j
@Tag(name = "功能管理", description = "功能管理相关接口")
@RestController
@RequestMapping("function")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private JwtUserServiceImpl jwtUserService;


    /**
     * 根据父编号查询菜单
     *
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/findMenuByPNumber")
    @Operation(summary = "根据父编号查询菜单")
    public Resp<JSONArray> findMenuByPNumber(@RequestBody JSONObject jsonObject,
                                             HttpServletRequest request) throws Exception {
        String pNumber = jsonObject.getString("pNumber");
        String userId = jsonObject.getString("userId");
        //存放数据json数组
        JSONArray dataArray = new JSONArray();
        try {
            Long roleId = 0L;
            String fc = "";
            List<SysUserBusiness> roleList = userBusinessService.getBasicData(userId, "UserRole");
            if (roleList != null && roleList.size() > 0) {
                String value = roleList.get(0).getValue();
                if (StringUtils.isNotEmpty(value)) {
                    String roleIdStr = value.replace("[", "").replace("]", "");
                    roleId = Long.parseLong(roleIdStr);
                }
            }
            //当前用户所拥有的功能列表，格式如：[1][2][5]
            List<SysUserBusiness> funList = userBusinessService.getBasicData(roleId.toString(), "RoleFunctions");
            if (funList != null && funList.size() > 0) {
                fc = funList.get(0).getValue();
            }
            List<SysFunction> dataList = functionService.getRoleFunction(pNumber);
            if (dataList.size() != 0) {
                dataArray = getMenuByFunction(dataList, fc);
                //增加首页菜单项
                JSONObject homeItem = new JSONObject();
                homeItem.put("id", 0);
                homeItem.put("text", "首页");
                homeItem.put("icon", "home");
                homeItem.put("url", "/dashboard/analysis");
                homeItem.put("component", "/layouts/TabLayout");
                dataArray.add(0, homeItem);
            }
        } catch (DataAccessException e) {
            log.error(">>>>>>>>>>>>>>>>>>>查找异常", e);
            return new Resp.Builder<JSONArray>().fail();
        }
        return new Resp.Builder<JSONArray>().setData(dataArray).ok();
    }

    public JSONArray getMenuByFunction(List<SysFunction> dataList, String fc) throws Exception {
        JSONArray dataArray = new JSONArray();
        for (SysFunction function : dataList) {
            JSONObject item = new JSONObject();
            List<SysFunction> newList = functionService.getRoleFunction(function.getNumber());
            item.put("id", function.getId());
            item.put("text", function.getName());
            item.put("icon", function.getIcon());
            item.put("url", function.getUrl());
            item.put("component", function.getComponent());
            if (newList.size() > 0) {
                JSONArray childrenArr = getMenuByFunction(newList, fc);
                if (childrenArr.size() > 0) {
                    item.put("children", childrenArr);
                    dataArray.add(item);
                }
            } else {
                if (fc.contains("[" + function.getId().toString() + "]")) {
                    dataArray.add(item);
                }
            }
        }
        return dataArray;
    }

    /**
     * 角色对应功能显示
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/findRoleFunction")
    @Operation(summary = "角色对应功能显示")
    public Resp<JSONArray> findRoleFunction(@RequestParam("roleId") Long roleId,
                                            @RequestParam("projectId") Long projectId,
                                            @RequestParam("userId") Long userId,
                                            HttpServletRequest request) throws Exception {
        JSONArray arr = new JSONArray();
        try {
            List<SysFunction> dataListFun = functionService.findRoleFunction("0");
            //开始拼接json数据
            JSONObject outer = new JSONObject();
            outer.put("id", 0);
//            outer.put("key", 0);
            outer.put("value", 0);
            outer.put("title", "功能列表");
//            outer.put("attributes", "功能列表");
            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataListFun) {
                //根据条件从列表里面移除"系统管理"
                List<SysFunction> dataList = new ArrayList<>();
                for (SysFunction fun : dataListFun) {
                    SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
                    dataList.add(fun);
                }
                dataArray = getFunctionList(dataList, "RoleFunctions", roleId, projectId, userId);
                outer.put("model", dataArray);
            }
            arr.add(outer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Resp.Builder<JSONArray>().setData(arr).ok();
    }

    public JSONArray getFunctionList(List<SysFunction> dataList,
                                     String type,
                                     Long roleId,
                                     Long projectId,
                                     Long userId) throws Exception {
        JSONArray dataArray = new JSONArray();
        //获取权限信息
        SysUserBusiness sysUserBusiness = userBusinessService.getRoleProjectFunction(roleId, projectId, userId);
        String ubValue = "";
        if (sysUserBusiness == null) {
            ubValue = "[]";
        } else {
            ubValue = StringUtils.isNotEmpty(sysUserBusiness.getValue()) ? sysUserBusiness.getValue() : "[]";
        }

        String invisibleList = sysUserBusiness.getInvisible();
        if(invisibleList==null){
            invisibleList = "[]";
        }


//        String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, keyId);
        if (null != dataList) {
            for (SysFunction function : dataList) {
                JSONObject item = new JSONObject();

                //可见的才显示出来
                if(!invisibleList.contains("[" + function.getId().toString() + "]")){
                    Boolean checked = ubValue.contains("[" + function.getId().toString() + "]");
                    item.put("id", function.getId());
                    item.put("value", function.getId());
                    item.put("title", function.getName());
                    item.put("checked", checked);
                    List<SysFunction> funList = functionService.findRoleFunction(function.getNumber());
                    if (funList.size() > 0) {
                        JSONArray funArr = getFunctionList(funList, "RoleFunctions", roleId, projectId, userId);
                        item.put("children", funArr);
                        dataArray.add(item);
                    } else {
                        Boolean flag = ubValue.contains("[" + function.getId().toString() + "]");
                        item.put("checked", flag);
                        dataArray.add(item);
                    }
                }


            }
        }
        return dataArray;
    }

    /**
     * 根据id列表查找功能信息
     *
     * @param roleId
     * @return
     */
    @GetMapping(value = "/findRoleFunctionsById")
    @Operation(summary = "根据id列表查找功能信息")
    public Resp<JSONObject> findByIds(@RequestParam("roleId") Long roleId) {
        try {
            List<SysUserBusiness> list = userBusinessService.getBasicData(roleId.toString(), "RoleFunctions");
            if (null != list && list.size() > 0) {
                //按钮
                Map<Long, String> btnMap = new HashMap<>();
                String btnStr = list.get(0).getBtnStr();
                if (StringUtils.isNotEmpty(btnStr)) {
                    JSONArray btnArr = JSONArray.parseArray(btnStr);
                    for (Object obj : btnArr) {
                        JSONObject btnObj = JSONObject.parseObject(obj.toString());
                        if (btnObj.get("funId") != null && btnObj.get("btnStr") != null) {
                            btnMap.put(btnObj.getLong("funId"), btnObj.getString("btnStr"));
                        }
                    }
                }
                //菜单
                String funIds = list.get(0).getValue();
                funIds = funIds.substring(1, funIds.length() - 1);
                funIds = funIds.replace("][", ",");
                List<SysFunction> dataList = functionService.findByIds(funIds);
                JSONObject outer = new JSONObject();
                outer.put("total", dataList.size());
                //存放数据json数组
                JSONArray dataArray = new JSONArray();
                if (null != dataList) {
                    for (SysFunction function : dataList) {
                        JSONObject item = new JSONObject();
                        item.put("id", function.getId());
                        item.put("name", function.getName());
                        item.put("pushBtn", function.getPushBtn());
                        item.put("btnStr", btnMap.get(function.getId()));
                        dataArray.add(item);
                    }
                }
                outer.put("rows", dataArray);
                return new Resp.Builder<JSONObject>().setData(outer).ok();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<JSONObject>().fail();

        }
        return new Resp.Builder<JSONObject>().ok();
    }

    /**
     * 保存用户角色功能/修改用户角色功能
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/saveRoleFunction")
    @Operation(summary = "保存用户角色功能")
    public Resp<Object> saveRoleFunction(@RequestBody @Valid RoleProjectFunctionDTO dto) {

        SysUserBusiness sysUserBusiness = userBusinessService.getRoleProjectFunction(dto.getRoleId(), dto.getProjectId(), dto.getUserId());

        if (null != sysUserBusiness) {
            BeanUtils.copyProperties(dto, sysUserBusiness);
            if (dto.getFunctionList().size() > 0) {
                sysUserBusiness.setBtnStr(JSON.toJSONString(dto.getFunctionList()));
            }
            sysUserBusiness.setType("RoleFunctions");
            // 更新权限
            userBusinessService.updateByPrimaryKey(sysUserBusiness);
           return new Resp.Builder<>().ok();
        } else {
            // 新增权限
            sysUserBusiness = new SysUserBusiness();
            BeanUtils.copyProperties(dto, sysUserBusiness);
            if (dto.getFunctionList().size() > 0) {
                sysUserBusiness.setBtnStr(JSON.toJSONString(dto.getFunctionList()));
            }
            sysUserBusiness.setType("RoleFunctions");
            userBusinessService.insert(sysUserBusiness);
            return new Resp.Builder<>().ok();
        }
    }
}