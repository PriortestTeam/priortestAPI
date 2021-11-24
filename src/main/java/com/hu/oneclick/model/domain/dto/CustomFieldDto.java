package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * 自定义字段表(CustomFieldDto)dto
 *
 * @author masiyi
 * @since 2021年11月17日14:33:40
 */
@Data
public class CustomFieldDto{

    /**
     * 作用范围，范围以逗号分隔
     */
    private String scope;


    private String projectId;




}
