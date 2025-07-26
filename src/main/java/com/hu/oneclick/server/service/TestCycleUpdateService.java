
package com.hu.oneclick.server.service;

import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;

/**
 * 测试周期更新服务
 *
 * @author oneclick
 */
public interface TestCycleUpdateService {
    
    /**
     * 更新测试周期
     *
     * @param dto 测试周期保存DTO
     * @return 更新后的测试周期
     */
    TestCycle update(TestCycleSaveDto dto);
    
    /**
     * 获取测试周期详情
     *
     * @param id 测试周期ID
     * @return 测试周期详情
     */
    TestCycle info(Long id);
}
