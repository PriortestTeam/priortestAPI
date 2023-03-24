package com.hu.oneclick.model.domain.dto;

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

    private Long customFieldId;

    private String possibleValue;

    private Date updateTime;

    private Long modifyUserId;
}
