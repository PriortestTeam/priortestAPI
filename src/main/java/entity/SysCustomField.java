package entity;

import java.io.Serializable;

/**
 * (SysCustomField)实体类
 *
 * @author makejava
 * @since 2021-04-01 10:57:30
 */
public class SysCustomField implements Serializable {
    private static final long serialVersionUID = 314442305554804583L;

    private Long id;
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldNameCn() {
        return fieldNameCn;
    }

    public void setFieldNameCn(String fieldNameCn) {
        this.fieldNameCn = fieldNameCn;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }

}
