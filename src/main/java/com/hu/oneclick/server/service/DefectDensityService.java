
package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.DefectDensityRequestDto;
import com.hu.oneclick.model.domain.dto.DefectDensityResponseDto;

/**
 * 缺陷密度服务接口
 */
public interface DefectDensityService {
    
    /**
     * 计算缺陷密度
     * @param requestDto 请求参数
     * @return 缺陷密度计算结果，包含缺陷详情和关联测试用例信息
     */
    DefectDensityResponseDto calculateDefectDensity(DefectDensityRequestDto requestDto);
}
