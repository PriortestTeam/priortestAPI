package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SysOrderDiscountDto;
import com.hu.oneclick.server.service.SysOrderDiscountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author masiyi
 */
@RestController
@RequestMapping("userOrderdisCount")
@Api(tags = "订单折扣模块")
public class SysOrderDiscountController {

    @Autowired
    private SysOrderDiscountService sysOrderDiscountService;

    @ApiOperation("计算订单价格")
    @PostMapping("calculateOrderPrice")
    public Resp<Map<String, BigDecimal>> calculateOrderPrice(@RequestBody SysOrderDiscountDto sysOrderDiscountDto) {
        return sysOrderDiscountService.calculateOrderPrice(sysOrderDiscountDto);
    }


}
