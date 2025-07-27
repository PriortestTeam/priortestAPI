
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 执行用例详情DTO
 */
@Data
public class ExecutedCaseDto {
    
    /**
     * 测试用例ID
     */
    private Long testCaseId;
    
    /**
     * 运行用例ID
     */
    private Long runCaseId;
    
    /**
     * 执行状态
     */
    private Integer runStatus;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 执行时间
     */
    private LocalDateTime executionTime;
    
    /**
     * 测试用例标题
     */
    private String testCaseTitle;
}
