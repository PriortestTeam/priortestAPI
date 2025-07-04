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

}
