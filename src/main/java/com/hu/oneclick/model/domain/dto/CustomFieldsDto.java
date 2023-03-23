package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldsDto {

    @JsonProperty("custom_field_id")
    private Long customFieldId;

    @JsonProperty("possible_value")
    private String possibleValue;

    private Date updateTime;

    private Long modifyUserId;
}
