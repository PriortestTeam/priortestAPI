package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义文本字段(FieldText)实体类
 *
 * @author qingyang
 * @since 2020-12-05 20:02:02
 */
public class FieldText extends CustomField implements Serializable {
    private static final long serialVersionUID = 613936107743960355L;

    private String customFieldId;

    private String defaultValue;

    private String content;
    /**
     * 设定字符的长度
     */
    private Integer length;

    private List<String> defaultValues;

    @Override
    public void subVerify(){
        super.verify();

        if(defaultValues != null) {
            final int[] strLength = {30};
            int defaultValuesSize = 5;
            if (this.defaultValues.size() < defaultValuesSize) {
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "数组下标应为" + defaultValuesSize + "个！");
            }
            defaultValues.forEach(e -> {
                if (length != null && length < strLength[0]) {
                    strLength[0] = length;
                }
                if (defaultValues.size() > strLength[0]) {
                    throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "文本内容不得超过" + strLength[0] + "个字符。");
                }
            });
            //数组转为字符串逗号分隔
            this.defaultValue = convertToString(defaultValues);
        }

        this.setCustomFieldId();

        this.setType();
    }

    public String convertToString(List<String> strings){
        StringBuilder sb = new StringBuilder();

        for(int i = 0;i < strings.size(); i++){
            if (StringUtils.isEmpty(strings.get(i))){
                sb.append(OneConstant.COMMON.REPLACE_EMPTY_CHARACTERS);
            }else {
                sb.append(strings.get(i));
            }

            if (i == strings.size() - 1){
                break;
            }
            sb.append(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
        }
        return sb.toString();
    }

    public List<String> convertToList(String str){
        List<String> strings = Arrays.asList(str.split(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER));
        for (int i = 0; i < strings.size(); i++){
            if (strings.get(i).equals(OneConstant.COMMON.REPLACE_EMPTY_CHARACTERS)){
                strings.set(i,"");
            }
        }
        return strings;
    }

    @Override
    public void setType() {
        super.setType(OneConstant.CUSTOM_FIELD_TYPE.TEXT);
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId() {
        this.customFieldId = super.getId();
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

    public List<String> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(List<String> defaultValues) {
        this.defaultValues = defaultValues;
    }
}
