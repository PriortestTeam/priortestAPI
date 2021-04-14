package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.model.domain.SysCustomField;
import com.hu.oneclick.model.domain.SysCustomFieldExpand;

import java.util.List;

public class SysCustomFieldVo {

    private SysCustomField sysCustomField;

    private SysCustomFieldExpand sysCustomFieldExpand;

    private List<String> mergeValues;


    public SysCustomField getSysCustomField() {
        return sysCustomField;
    }

    public void setSysCustomField(SysCustomField sysCustomField) {
        this.sysCustomField = sysCustomField;
    }

    public SysCustomFieldExpand getSysCustomFieldExpand() {
        return sysCustomFieldExpand;
    }

    public void setSysCustomFieldExpand(SysCustomFieldExpand sysCustomFieldExpand) {
        this.sysCustomFieldExpand = sysCustomFieldExpand;
    }

    public List<String> getMergeValues() {
        return mergeValues;
    }

    public void setMergeValues(List<String> mergeValues) {
        this.mergeValues = mergeValues;
    }
}
