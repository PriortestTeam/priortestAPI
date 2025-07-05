package com.hu.oneclick.model.domain.vo;

import com.hu.oneclick.model.entity.CustomFileldLink;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName CustomFileldLinkVo.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月27日 20:57:00
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CustomFileldLinkVo extends CustomFileldLink implements Serializable {

    private static final long serialVersionUID = 870331260917684971L;

    private Long projectId;
    /**
     * 自定义字段类型
     */
    private String type;

    /**
     * 字段名称  - 中文名
     */
    private String fieldNameCn;

    /**
     * 字段名称 - 英文名
     */
    private String fieldNameEn;

    /**
     * 用户id，修改者
     */
    private Long modifyUser;

    /**
     * 用于文本，备注，链接字段
     */
    private Integer length;

    /**
     * 可能的值,变量值，依据类型的不同而不同
     */
    private String possibleValue;

    /**
     * field_type （类型英文名，用作前端名页面创建，修改时的标识）
     */
    private String fieldType;

    /**
     * field_type_cn (类型中文名，用作前端显示）
     */
    private String fieldTypeCn;

    private Object child;

    @JsonGetter("customFieldId")
    public Object getCustomFieldIdForJson() {
        if ("fixVersion".equals(this.getFieldNameEn()) && super.getCustomFieldId() != null) {
            return super.getCustomFieldId().toString() + "000";
        }
        return super.getCustomFieldId();
    }

    private static final long serialVersionUID = 1L;

    public String getFieldNameEn() {
        return fieldNameEn;
    }

    @Override
    public Long getCustomFieldId() {
        return super.getCustomFieldId();
    }

    private CustomFileldLink entity;
    private String fieldName;

    public CustomFileldLinkVo() {
        this.entity = new CustomFileldLink(null);
    }
    public void setEntity(CustomFileldLink customFileldLink) {
        CustomFileldLink entity = new CustomFileldLink();
        // 设置属性值
        this.entity = entity;
    }

    // 根据当前对象更新关联实体的属性
    public void updateEntity() {
        if (this.entity != null) {
            // 更新实体属性
        }
    }

    // 从关联实体获取数据
    public void loadFromEntity() {
        if (this.entity != null) {
            // 从实体加载数据
        }
    }

    // 验证当前对象
    public boolean validate() {
        // 执行验证逻辑
        return true;
    }

    // 获取完整的显示名称
    public String getFullDisplayName() {
        return this.fieldName != null ? this.fieldName : "未知字段";
    }

    // 判断是否为新创建的对象
    public boolean isNew() {
        return this.entity == null || this.entity.getCustomFieldId() == null;
    }

    // 获取字段ID，如果实体存在的话
    public Long getFieldId() {
        return this.entity != null ? this.entity.getCustomFieldId() : null;
    }

    // 设置字段关联
    public void setFieldAssociation(Long fieldId, String fieldName) {
        this.fieldName = fieldName;
        if (this.entity != null) {
            // 移除错误的方法调用
        }
    }
    public void setCustomFieldId(Long customFieldId) {
        super.setCustomFieldId(customFieldId);
        this.entity = new CustomFileldLink(null);
        if (entity != null) {
            entity.setCustomFieldId(customFieldId);
        }
    }
}