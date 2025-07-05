package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCaseTemplateJson;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xwf
 * @date 2021/8/4 22:18
 */
@Service
public interface TestCaseTemplateJsonService {
    /**
     * 插入
     * @param testCaseTemplateJson
     * @return
     */
    Resp<String> insert(TestCaseTemplateJson testCaseTemplateJson);

    /**
     * 更新
     * @param testCaseTemplateJson
     * @return
     */
    Resp<String> update(TestCaseTemplateJson testCaseTemplateJson);

    /**
     * 获取当前登录人模板
     *
     * @return
     */
    Resp<List<TestCaseTemplateJson>> queryListByUserId();

    Resp<String> deleteById(String id);

    Resp<TestCaseTemplateJson> queryById(String id);
}
