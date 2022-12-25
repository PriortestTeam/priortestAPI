package com.hu.oneclick.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 下拉菜单
 * </p>
 *
 * @author vince
 * @since 2022-12-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFileldLink implements Serializable {

    private static final long serialVersionUID = 870331260917684970L;
    /**
     * 主键
     */
    private Long customFieldLinkId;

    private Long customFieldId;

    /**
     * 字段应用范围 - 属性：默认值
     */
    private String defaultValue;

    /**
     * 字段应用范围 - 属性：英文
     */
    private String scope;

    /**
     * 字段应用范围 - 属性：必填
     */
    private Integer mandatory;

    /**
     * 字段应用范围 - 属性：范围id
     */
    private Long scopeId;
    /**
     * 字段应用范围 - 属性：范围中文
     */
    private String scopeCn;

    public CustomFileldLink(Long customFieldId) {
        this.customFieldId = customFieldId;
    }
}
