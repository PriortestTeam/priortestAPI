package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;

import java.io.Serializable;

/**
 * 自定义文本字段(FieldText)实体类
 *
 * @author qingyang
 * @since 2020-12-05 20:02:02
 */
public class FieldText extends CustomField implements Serializable {
    private static final long serialVersionUID = 613936107743960355L;

    private String customFieldId = super.getId();

    private String defaultValue;

    private String content;
    /**
     * 设定字符的长度
     */
    private Integer length;

    @Override
    public void subVerify(){
        super.verify();

        int strLength = 30;
        if (defaultValue.length() > strLength || content.length() > strLength){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"文本内容不得超过30个字符。");
        }

    }


    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
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
