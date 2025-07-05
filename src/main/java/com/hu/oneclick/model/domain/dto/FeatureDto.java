package com.hu.oneclick.model.domain.dto;

import com.alibaba.fastjson2.JSONArray;
import com.hu.oneclick.model.entity.Feature;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author masiyi
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class FeatureDto extends Feature {
    private ViewTreeDto viewTreeDto;

    private String filter;

    private JSONArray sysCustomField;



}
