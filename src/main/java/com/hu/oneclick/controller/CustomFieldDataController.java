package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.server.service.CustomFieldDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/27
 * @since JDK 1.8.0
 */
@RestController
@Api(tags = "用户自定义字段存储值")
@RequestMapping("CustomFieldData")
public class CustomFieldDataController {

    @Autowired
    private CustomFieldDataService customFieldDataService;

    @ApiOperation("添加项目自定义值")
    @PostMapping("insertProjectCustomData")
    public Resp<String> insertProjectCustomData(@RequestBody List<CustomFieldData> customFieldDatas) {

        return Result.addResult(customFieldDataService.insertProjectCustomData(customFieldDatas));

    }

}
