package com.hu.oneclick.model.entity;
import lombok.Data;
import java.io.Serializable;
/**
 * @ClassName SysConfig.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月24日 18:21:00
 */
@Data

public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 范围名称
     */
    private String scopeName;
    /**
     * sort
     */
    private Integer sort;
    /**
     * 中文名称
     */
    private String nameCn;
    /**
     * group
     */
    private String group;
}
}
}
