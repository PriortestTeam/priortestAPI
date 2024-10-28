package com.hu.oneclick.model.domain.dto;

import com.alibaba.fastjson.JSONArray;
import com.hu.oneclick.model.entity.Feature;
import lombok.Data;

/**
 * @author masiyi
 */
@Data
public class FeatureDto extends Feature {
    private ViewTreeDto viewTreeDto;

    private String filter;

    private JSONArray sysCustomField;



}
