package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.domain.SysCustomField;
import com.hu.oneclick.model.domain.SysCustomFieldExpand;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SysCustomFieldVo implements VerifyParam {

    private SysCustomField sysCustomField;

    private SysCustomFieldExpand sysCustomFieldExpand;

    /**
     *   返回使用，下拉框数据
     */
    private List<String> mergeValues;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(sysCustomField.getId())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"字段ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(StringUtils.isEmpty(sysCustomField.getFieldName())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"字段名" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }


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
