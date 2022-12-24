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
     * 默认下拉菜单第一项
     */
    private String defaultValue;

    private String scope;

    private Integer mandatory;

    private Long scopeId;

    public CustomFileldLink(Long customFieldId) {
        this.customFieldId = customFieldId;
    }
}
