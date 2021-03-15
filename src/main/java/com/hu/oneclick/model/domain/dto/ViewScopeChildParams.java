package com.hu.oneclick.model.domain.dto;

import java.util.List;

/**
 * @author qingyang
 */
public class ViewScopeChildParams {

    private String filedName;

    private String filedNameCn;

    private String type;

    /**
     * 下边几个参数，下拉选项框使用
     */
    private List<ViewScopeChildParams> selectChild;
    private String optionValue;
    private String optionValueCn;


    public ViewScopeChildParams(String type,String filedName, String filedNameCn) {
        this.filedName = filedName;
        this.filedNameCn = filedNameCn;
        this.type = type;
    }

    public ViewScopeChildParams(String type,String filedName, String filedNameCn, List<ViewScopeChildParams> selectChild) {
        this.filedName = filedName;
        this.filedNameCn = filedNameCn;
        this.type = type;
        this.selectChild = selectChild;
    }



    public ViewScopeChildParams(String optionValue, String optionValueCn) {
        this.optionValue = optionValue;
        this.optionValueCn = optionValueCn;
    }

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

    public List<ViewScopeChildParams> getSelectChild() {
        return selectChild;
    }

    public void setSelectChild(List<ViewScopeChildParams> selectChild) {
        this.selectChild = selectChild;
    }
}
