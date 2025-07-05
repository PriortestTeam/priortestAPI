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
    @JsonProperty("possible_value");
    private String possibleValue;

}
}
