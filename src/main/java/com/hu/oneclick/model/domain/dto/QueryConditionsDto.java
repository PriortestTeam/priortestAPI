
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.util.List;

/**
 * 查询条件记录DTO
 */
@Data
public class QueryConditionsDto {
    
    /**
     * 项目ID
     */
    private Long projectId;
    
    /**
     * 查询的版本列表
     */
    private List<String> versions;
    
    /**
     * 测试周期ID列表
     */
    private List<Long> testCycleIds;
    
    /**
     * 是否包含所有测试周期
     */
    private Boolean includeAllCycles;
}
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.util.List;

/**
 * 查询条件记录DTO
 */
@Data
public class QueryConditionsDto {
    
    /**
     * 项目ID
     */
    private Long projectId;
    
    /**
     * 主版本号
     */
    private String majorVersion;
    
    /**
     * 包含的版本列表
     */
    private List<String> includeVersions;
    
    /**
     * 测试周期ID列表
     */
    private List<Long> testCycleIds;
}
