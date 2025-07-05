package com.hu.oneclick.model.entity;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import java.io.Serializable;
import java.util.List;
/**
 * 下拉菜单(FieldDropDown)实体类
 *
 * @author qingyang
 * @since 2020-12-05 21:49:12
 */


public class FieldDropDown extends CustomField implements Serializable {
    private static final long serialVersionUID = 668370284822611568L;
    private String customFieldId;
    /**
     * 默认下拉菜单第一项
     */
    private String defaultValue;
    /**
     * 数组格式，每一个值按照 逗号分隔
     */
    private String dropDownList;
    /**
     * 用于判断用户输入的参数
     */
    private List&lt;String> dropDowns;
    /**
     * 数组中字符串长度,默认10
     */
    private Integer length = 30;
    @Override
    public void subVerify() {
        super.verify();
        this.setCustomFieldId();
        this.setType();
        if (dropDowns != null){
            this.dropDownList = TwoConstant.convertToString(dropDowns,length);
        }
    }
    @Override
    public void setType() {
        super.setType(OneConstant.CUSTOM_FIELD_TYPE.DROP_DOWN);
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
    public String getDropDownList() {
        return dropDownList;
    }
    public void setDropDownList(String dropDownList) {
        this.dropDownList = dropDownList;
    }
    public List&lt;String> getDropDowns() {
        return dropDowns;
    }
    public void setDropDowns(List&lt;String> dropDowns) {
        this.dropDowns = dropDowns;
    }
    public Integer getLength() {
        return length;
    }
    public void setLength(Integer length) {
        this.length = length;
    }
}
}
}
