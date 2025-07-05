package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 故事(Feature)实体类
 *
 * @author makejava
 * @since 2021-02-03 13:54:35
 */
@Data
public class FeatureSaveDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "关联项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 记录
     */
    private String epic;

    /**
     * 指派给谁
     */
    private String reportTo;

    /**
     * 状态
     */
    private String featureStatus;
    /**
     * 版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;

    /**
     * 名称
     */
    private String title;


    private String module;

    private String remarks;

    /**
     * 自定义字段值
     */
    @Schema(description = "自定义字段值")
    private JSONObject customFieldDatas;
}
package com.hu.oneclick.model.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureSaveDto {
    
    private Long id;
    
    private String title;
    
    private Long projectId;
    
    private String description;
    
    public Long getId() {
        return id;
    }
}
