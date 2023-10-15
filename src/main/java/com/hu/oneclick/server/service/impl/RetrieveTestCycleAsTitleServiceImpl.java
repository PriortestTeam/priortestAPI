package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.vo.TestCycleVo;
import com.hu.oneclick.server.service.RetrieveTestCycleAsTitleService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author cheng
 */
@Service
public class RetrieveTestCycleAsTitleServiceImpl implements RetrieveTestCycleAsTitleService {

    @NonNull
    @Resource
    private TestCycleDao testCycleDao;

    @Override
    public Resp<TestCycleVo> getIdForTitle(String title, Long projectId) {
        Long id = testCycleDao.getIdByTitle(title, projectId);
        if (id == null) {
            throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "查询失败，参数有误");
        }
        TestCycleVo testCycleVo = new TestCycleVo();
        testCycleVo.setId(id);
        return new Resp.Builder<TestCycleVo>().setData(testCycleVo).ok();
    }
}
