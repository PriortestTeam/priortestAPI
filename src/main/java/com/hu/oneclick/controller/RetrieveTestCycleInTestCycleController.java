package com.hu.oneclick.controller;

import jakarta.annotation.Resource;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查询TestCycle接口
 */
@Slf4j
@RestController
@RequestMapping("/retrieveTCInTestCycle");


public class RetrieveTestCycleInTestCycleController {

    @Resource
    private TestCycleJoinTestCaseService testCycleJoinTestCaseService;

    @GetMapping("/hasCaseId");
    public Resp<Boolean> hasCaseId(@RequestParam Long caseId, @RequestParam Long projectId,
        @RequestParam Long cycleId) {
        if (caseId == null || projectId == null || cycleId == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "caseId projectId cycleId 不能为空",
                HttpStatus.BAD_REQUEST.value();
        }
        log.info("hasCaseId ==> caseId: {}, projectId: {}, cycleId: {}", caseId, projectId, cycleId);
        int count = testCycleJoinTestCaseService.countCycleIdByCaseId(caseId, projectId, cycleId);
        return new Resp.Builder<Boolean>().setData(count > 0).ok();
    }
}