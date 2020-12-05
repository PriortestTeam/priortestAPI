package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.exception.BizException;

import java.io.Serializable;

/**
 * 单选框(FieldRadio)实体类
 *
 * @author makejava
 * @since 2020-12-04 15:38:40
 */
public class FieldRadio extends CustomField implements Serializable {
    private static final long serialVersionUID = -46014189887598954L;

    private String customFieldId = super.getId();
    /**
     * 0 , 1  单选框  0 未选中，1 选中
     */
    private Integer defaultValue;
    /**
     * 0 , 1  单选框  0 未选中，1 选中
     */
    private Integer value;


    @Override
    public void subVerify() throws BizException {
        super.verify();
    }

    /**
     * 设置type 类型 为 radio
     */
    @Override
    public void setType() {
        super.setType(OneConstant.CUSTOM_FIELD_TYPE.RADIO);
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }


}
