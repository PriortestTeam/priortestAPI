package com.hu.oneclick.controller;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查询TestCycle接口
 */
@Slf4j
@RestController
@RequestMapping("/retrieveTCInTestCycle")
public class RetrieveTestCycleInTestCycle {

    @Resource
    private TestCycleJoinTestCaseService testCycleJoinTestCaseService;

    @GetMapping("/hasCaseId")
    public Resp<Boolean> hasCaseId(@RequestParam Long caseId) {

        if (caseId == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "caseId不能为空");
        }
        log.info("getcaseId ==> caseId:{}", JSON.toJSONString(caseId));
        int count = testCycleJoinTestCaseService.countCycleIdByCaseId(caseId);
        return new Resp.Builder<Boolean>().setData(count > 0).ok();
    }
}
