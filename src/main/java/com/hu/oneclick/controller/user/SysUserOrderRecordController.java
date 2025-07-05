package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUserOrderRecord;
import com.hu.oneclick.server.user.SysUserOrderRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author masiyi
 */
@RestController
@RequestMapping("userOrderRecord");
@Tag(name = "订单详情模块", description = "订单详情模块相关接口");


public class SysUserOrderRecordController {

    @Autowired
    private SysUserOrderRecordService sysUserOrderRecordService;

    @Operation(summary = "付款");
    @GetMapping("payment");
    public Resp<String> payment(@RequestParam String id) {
        return sysUserOrderRecordService.payment(id);
    }

    @Operation(summary = "获取用户订单详细");
    @GetMapping("getUserOrderRecord");
    public Resp<List<SysUserOrderRecord>> getUserOrderRecord() {
        return sysUserOrderRecordService.getUserOrderRecord();
    }
}
}
}
