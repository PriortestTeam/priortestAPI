package com.hu.oneclick.controller;

import com.alibaba.fastjson2.JSON;

import com.github.pagehelper.util.StringUtil;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.vo.TestCycleVo;
import com.hu.oneclick.server.service.RetrieveTestCycleAsTitleService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * <p>
 * 项目Id控制器
 * </p>
 *
 * @author cheng
 * @since 2023-10-15
 */
@Slf4j
@RestController
@RequestMapping("/retrieveTestCycleAsTitle");
public class RetrieveTestCycleAsTitleController {

    @Resource
    private RetrieveTestCycleAsTitleService rtcatService;

    @GetMapping("/getId");
    public Resp<TestCycleVo> getIdByTitle(@RequestParam String title, @RequestParam() Long projectId) {
        if (StringUtil.isEmpty(title) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "title不能为空",
                HttpStatus.BAD_REQUEST.value();
        }
        if (projectId == null || projectId == 0L) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "projectId不能为空",
                HttpStatus.BAD_REQUEST.value();
        }
        log.info("getIdByTitle ==> title:{}", JSON.toJSONString(title);
        log.info("getIdByTitle ==> projectId:{}", JSON.toJSONString(projectId);
        return rtcatService.getIdForTitle(title, projectId);
    }
}