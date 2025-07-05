package com.hu.oneclick.model.entity;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import java.io.Serializable;
import java.util.List;
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
     * 设定字符的长度,默认300
     */
    private Integer length = 300;
    private List&lt;String> defaultValues;
    @Override
    public void subVerify(){
        super.verify();
        if(defaultValues != null) {
            int defaultValuesSize = 5;
            if (this.defaultValues.size() < defaultValuesSize) {
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "数组下标应为" + defaultValuesSize + "个！");
            }
            defaultValues.forEach(e -> {
                if (defaultValues.size() > length) {
                    throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "文本内容不得超过" + length + "个字符。");
                }
            });
            //数组转为字符串逗号分隔
            this.defaultValue = TwoConstant.convertToString(defaultValues,length);
        }
        this.setCustomFieldId();
        this.setType();
    }
    @Override
    public void setType() {
        super.setType(OneConstant.CUSTOM_FIELD_TYPE.RICH_TEXT);
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
    public List&lt;String> getDefaultValues() {
        return defaultValues;
    }
    public void setDefaultValues(List&lt;String> defaultValues) {
        this.defaultValues = defaultValues;
    }
}
}
}
