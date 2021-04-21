package com.hu.oneclick.model.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingyang
 */
public class ViewScopeChildParams implements Serializable {

    private String filedName;

    private String filedNameCn;

    private String type;

    /**
     * 下边几个参数，下拉选项框使用
     */
    private List<ViewScopeChildParams> selectChild;
    private String optionValue;
    private String optionValueCn;

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getFiledNameCn() {
        return filedNameCn;
    }

    public void setFiledNameCn(String filedNameCn) {
        this.filedNameCn = filedNameCn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public List<ViewScopeChildParams> getSelectChild() {
        return selectChild;
    }

    public void setSelectChild(List<ViewScopeChildParams> selectChild) {
        this.selectChild = selectChild;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getOptionValueCn() {
        return optionValueCn;
    }

    public void setOptionValueCn(String optionValueCn) {
        this.optionValueCn = optionValueCn;
    }
}
