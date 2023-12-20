package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.server.service.TestCycleTcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class TestCycleTcServiceImpl implements TestCycleTcService {

    private TestCycleTcDao testCycleTcDao;

    private JwtUserServiceImpl jwtUserService;

    @Autowired
    public void setTestCycleTcDao(TestCycleTcDao testCycleTcDao) {
        this.testCycleTcDao = testCycleTcDao;
    }

    @Autowired
    public void setJwtUserServiceImpl(JwtUserServiceImpl jwtUserService) {
        this.jwtUserService = jwtUserService;
    }

    @Override
    public Resp<java.lang.String> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto) {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();

        // 获取当前时间
        Date currentDate = new Date();
        // 将当前时间转换为 Timestamp 对象
        executeTestCaseDto.setCreateTime(new Timestamp(currentDate.getTime()));

        int i = testCycleTcDao.addTestCaseExecution(sysUser.getId(), executeTestCaseDto);

        if (i > 0) {
            return new Resp.Builder<java.lang.String>().ok();
        }
        return new Resp.Builder<java.lang.String>().fail();
    }
}
