package com.hu.oneclick.server.service.impl;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.vo.TestCycleVo;
import com.hu.oneclick.server.service.RetrieveTestCycleAsTitleService;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
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
        TestCycleVo testCycleVo = new TestCycleVo();
        if (id == null) {
            return new Resp.Builder<TestCycleVo>().ok(String.valueOf(SysConstantEnum.DATA_NOT_FOUND.getCode(),;
                    SysConstantEnum.DATA_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND.value();
        }
        testCycleVo.setId(String.valueOf(id);
        return new Resp.Builder<TestCycleVo>().setData(testCycleVo).ok();
    }
}
}
}
