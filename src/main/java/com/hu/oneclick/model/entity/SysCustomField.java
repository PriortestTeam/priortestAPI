package com.hu.oneclick.model.entity;
import lombok.Data;
import java.io.Serializable;
/**
 * (SysCustomField)实体类
 *
 * @author makejava
 * @since 2021-04-11 14:40:13
 */
@Data

public class SysCustomField implements Serializable {
    private static final long serialVersionUID = 680119388243867579L;
    private String id;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 中文字段名
     */
    private String fieldNameCn;
    /**
     * 默认值
     */
    private String defaultValues;
    private String valueList;
    /**
     * type
     *
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    private String fieldType;
    /**
     * scope
     *
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    private String scope;
    private int chartDisplay;
    private int mandatory;
    private int uiDisplay;
    //private String unicodeId;
    private int length;
    private int allowAddedValue;
    private int sort;
}
}
}
