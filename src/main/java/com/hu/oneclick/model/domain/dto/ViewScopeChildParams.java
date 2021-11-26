package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingyang
 */
@Data
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


}
