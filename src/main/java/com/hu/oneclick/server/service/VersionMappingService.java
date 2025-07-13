
package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.VersionMappingDto;

import java.util.List;

public interface VersionMappingService {
    
    /**
     * 批量创建版本映射
     */
    void batchCreateMapping(VersionMappingDto mappingDto);
    
    /**
     * 更新版本映射
     */
    void updateMapping(VersionMappingDto mappingDto);
    
    /**
     * 删除版本映射
     */
    void deleteMapping(Long id);
    
    /**
     * 根据发布版本查询映射关系
     */
    List<VersionMappingDto> getMappingByRelease(Long releaseId);
    
    /**
     * 根据项目ID查询所有映射关系
     */
    List<VersionMappingDto> getMappingByProject(Long projectId);
    
    /**
     * 删除发布版本的所有映射关系
     */
    void deleteMappingByRelease(Long releaseId);
}
