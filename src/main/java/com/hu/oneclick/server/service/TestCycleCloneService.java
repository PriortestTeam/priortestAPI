
package com.hu.oneclick.server.service;

import java.util.List;

/**
 * 测试周期克隆服务
 *
 * @author oneclick
 */
public interface TestCycleCloneService {
    
    /**
     * 克隆测试周期
     *
     * @param ids 测试周期ID列表
     */
    void clone(List<Long> ids);
}
