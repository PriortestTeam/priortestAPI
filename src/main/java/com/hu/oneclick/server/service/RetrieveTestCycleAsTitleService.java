package com.hu.oneclick.server.service;

import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.vo.TestCycleVo;

/**
 * @author cheng
 */
public interface RetrieveTestCycleAsTitleService {

    Resp<TestCycleVo> getIdForTitle(String title, Long projectId);
}
