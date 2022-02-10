package com.hu.oneclick.model.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.model.domain.Feature;
import lombok.Data;

/**
 * @author masiyi
 */
@Data
public class FeatureDto extends Feature {
    private ViewTreeDto viewTreeDto;

    private String filter;

    private JSONObject sysCustomField;



}
