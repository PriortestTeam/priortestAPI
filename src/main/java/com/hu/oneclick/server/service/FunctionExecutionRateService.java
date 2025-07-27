
package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.FunctionExecutionRateRequestDto;
import com.hu.oneclick.model.domain.dto.FunctionExecutionRateResponseDto;

/**
 * 功能执行率报表服务接口
 */
public interface FunctionExecutionRateService {
    
    /**
     * 获取功能执行率报表
     * @param requestDto 请求参数
     * @return 功能执行率报表数据
     */
    FunctionExecutionRateResponseDto getFunctionExecutionRate(FunctionExecutionRateRequestDto requestDto);
}
