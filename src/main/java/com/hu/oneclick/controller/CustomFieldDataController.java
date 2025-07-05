package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.entity.SysCustomField;
import com.hu.oneclick.server.service.CustomFieldDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/27
 * @since JDK 1.8.0
 */
@RestController
@Tag(name = "用户自定义字段存储值", description = "用户自定义字段存储值相关接口")
@RequestMapping("CustomFieldData")
public class CustomFieldDataController {

    @Autowired
    private CustomFieldDataService customFieldDataService;

    /**
     * 新建时获取所有用户字段
     *
     * @Param: [customFieldDto]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    @PostMapping("getAllCustomField")
    @Operation(summary = "新建时获取所有用户字段")
    public Resp<List<Object>> getAllCustomField(@RequestBody CustomFieldDto customFieldDto) {
        return customFieldDataService.getAllCustomField(customFieldDto);
    }


    /**
     * 新建时获取所有系统字段
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/17
     */
    @GetMapping("getAllSysCustomField")
    @Operation(summary = "新建时获取所有系统字段")
    public Resp<List<SysCustomField>> getAllSysCustomField(@RequestParam String scope) {
        return customFieldDataService.getAllSysCustomField(scope);
    }


}
