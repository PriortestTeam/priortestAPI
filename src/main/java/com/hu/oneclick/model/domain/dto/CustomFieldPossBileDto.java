package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomFieldPossBileDto {
    /**
     * 保留字段下划线格式
     */
    @JsonProperty("possible_value")
    private String possibleValue;
    
    /**
     * 项目自定义扩展字段
     */
    @JsonProperty("possible_value_child")
    private String possibleValueChild;
    
    /**
     * 数据来源类型：system/project
     */
    private String sourceType;

}
