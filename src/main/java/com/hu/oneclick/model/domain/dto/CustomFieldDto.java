
package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * 自定义字段表(CustomFieldDto)dto
 *
 * @author masiyi
 * @since 2021年11月17日14:33:40
 */
@Data


public class CustomFieldDto {

    /**
     * 作用范围，范围以逗号分隔
     */
    private String scope;

    private String projectId;

    private String type;

    private Long scopeId;

    // 手动添加缺失的getter/setter方法
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
}
