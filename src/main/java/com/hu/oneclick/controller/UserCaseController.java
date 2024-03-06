package com.hu.oneclick.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.param.UserCaseParam;
import com.hu.oneclick.model.domain.vo.UserCaseVo;
import com.hu.oneclick.server.service.UserCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("userCase")
@Api(tags = "故事用例")
public class UserCaseController extends BaseController {

    @Resource
    private UserCaseService userCaseService;

    @PostMapping(value = "list")
    @ApiOperation("列表")
    public Resp<List<UserCaseVo>> listData(@RequestBody UserCaseParam reqEntity) {
        if (ObjectUtil.isEmpty(reqEntity)) {
            reqEntity = new UserCaseParam();
        }
        List<UserCaseVo> resultList = this.userCaseService.listData(reqEntity);
        return new Resp.Builder<List<UserCaseVo>>().setData(resultList).ok();
    }

    @PostMapping(value = "pageData")
    @ApiOperation(value = "分页列表")
    public Resp<PageInfo<UserCaseVo>> pageData(@RequestBody UserCaseParam reqEntity) {
        if (ObjectUtil.isEmpty(reqEntity)) {
            reqEntity = new UserCaseParam();
        }
        startPage();
        List<UserCaseVo> resultList = this.userCaseService.listData(reqEntity);
        return new Resp.Builder<PageInfo<UserCaseVo>>().setData(PageInfo.of(resultList)).ok();
    }

    @PostMapping(value = "getUserCaseById")
    @ApiOperation(value = "根据ID获取对象")
    public Resp<UserCaseVo> getUserCaseById(@RequestBody String id) {
        UserCaseVo resultEntity = this.userCaseService.getUserCaseInfoById(id);
        return new Resp.Builder<UserCaseVo>().setData(resultEntity).ok();
    }

    @PostMapping(value = "createUserCase")
    @ApiOperation(value = "创建一个故事用例")
    public Resp<Boolean> createUserCase(@RequestBody UserCaseParam reqEntity) {
        boolean result = this.userCaseService.insertUserCase(reqEntity);
        return new Resp.Builder<Boolean>().setData(result).ok();
    }

    @PostMapping(value = "updateUserCase")
    @ApiOperation(value = "修改故事用例")
    public Resp<Boolean> updateUserCase(@RequestBody UserCaseParam reqEntity) {
        boolean result = this.userCaseService.updateUserCase(reqEntity);
        return new Resp.Builder<Boolean>().setData(result).ok();
    }

    @PostMapping(value = "removeUserCaseById")
    @ApiOperation(value = "根据ID删除故事用例")
    public Resp<Boolean> removeUserCaseById(@RequestBody String id) {
        boolean result = this.userCaseService.removeUserCaseById(id);
        return new Resp.Builder<Boolean>().setData(result).ok();
    }
}
