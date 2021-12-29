package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserOrder;
import com.hu.oneclick.server.user.UserOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author masiyi
 */
@RestController
@RequestMapping("userOrder")
@Api(tags = "订单模块")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @ApiOperation("新增订单")
    @PostMapping("insertOrder")
    public Resp<String> insertOrder(@RequestBody SysUserOrder sysUserOrder) {
        return userOrderService.insertOrder(sysUserOrder);
    }

    @ApiOperation("添加详细信息")
    @PostMapping("insertUserDetail")
    public Resp<String> insertUserDetail(@RequestBody SysUser sysUser) {
        return userOrderService.insertUserDetail(sysUser);
    }

    @ApiOperation("查询付款方式")
    @PostMapping("getPaymentMethod")
    public Resp<List<String>> getPaymentMethod() {
        return userOrderService.getPaymentMethod();
    }

}
