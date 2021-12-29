package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
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

    /** 新建时获取所有用户字段
     * @Param: [customFieldDto]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    @PostMapping("getAllCustomField")
    @ApiOperation("新建时获取所有用户字段")
    public Resp<List<Object>> getAllCustomField(@RequestBody CustomFieldDto customFieldDto) {
        return customFieldDataService.getAllCustomField(customFieldDto);
    }

}
