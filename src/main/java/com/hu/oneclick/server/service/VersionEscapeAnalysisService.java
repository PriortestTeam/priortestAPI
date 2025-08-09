package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisRequestDto;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisResponseDto;

/**
 * 版本缺陷逃逸率分析服务接口
 */
public interface VersionEscapeAnalysisService {

    /**
     * 分析指定版本的缺陷逃逸率
     * 
     * 核心逻辑：
     * 1. 查找所有 introduced_version = analysisVersion 的缺陷
     * 2. 按发现版本（issue_version）分组统计
     * 3. 计算逃逸率 = 后续版本发现的缺陷数 / 该版本引入的总缺陷数
     * 4. 分析遗留缺陷情况（is_legacy = 1）
     * 5. 提供质量评估和改进建议
     * 
     * @param requestDto 分析请求参数
     * @return 缺陷逃逸率分析结果
     */
    VersionEscapeAnalysisResponseDto analyzeVersionEscapeRate(VersionEscapeAnalysisRequestDto requestDto);

    /**
     * 导出版本逃逸率分析报告
     * 
     * @param requestDto 分析请求参数
     * @return 报告文件路径或下载链接
     */
    String exportEscapeAnalysisReport(VersionEscapeAnalysisRequestDto requestDto);
}