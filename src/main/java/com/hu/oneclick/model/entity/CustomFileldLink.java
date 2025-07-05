package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;

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
    @TableId
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
    private String scopeNameCn;

    public CustomFileldLink(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    @Schema(description = "删除标识")
    private Integer delFlag;

    // 手动添加getCustomFieldId方法
    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }
}