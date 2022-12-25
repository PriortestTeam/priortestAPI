package com.hu.oneclick.model.domain.vo;

import lombok.Data;

/**
 * @ClassName ComponentAttributesVo.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月14日 18:00:00
 */
@Data
public class ComponentAttributesVo {
    private String scope;
    private Long scopeId;
    private String scopeCn;
    private String defaultValue;
    private Boolean mandatory;
}
