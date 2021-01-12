package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;

import java.io.Serializable;

/**
 * 富文本
 * @author qingyang
 */
public class FieldRichText extends CustomField implements Serializable {

    private static final long serialVersionUID = 613936107743960355L;

    private String customFieldId;

    private String defaultValue;

    private String content;
    /**
     * 设定字符的长度
     */
    private Integer length;

    @Override
    public void subVerify(){
        super.verify();

        int strLength = 300;

        if (length != null && length < strLength){
            strLength = length;
        }

        if (defaultValue.length() > strLength || content.length() > strLength){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"文本内容不得超过"+strLength+"个字符。");
        }

        this.setCustomFieldId();

        this.setType();

    }


    @Override
    public void setType() {
        super.setType(OneConstant.CUSTOM_FIELD_TYPE.TEXT);
    }


    public String getCustomFieldId() {
        return customFieldId;
    }


    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public void setCustomFieldId() {
        this.customFieldId =  super.getId();
    }


    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
