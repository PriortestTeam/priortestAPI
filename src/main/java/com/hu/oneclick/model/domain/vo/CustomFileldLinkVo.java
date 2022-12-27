package com.hu.oneclick.model.domain.vo;

import com.hu.oneclick.model.domain.CustomFileldLink;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName CustomFileldLinkVo.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月27日 20:57:00
 */
@Data
public class CustomFileldLinkVo extends CustomFileldLink implements Serializable {

    private static final long serialVersionUID = 870331260917684971L;

    private Long projectId;
    /**
     * 自定义字段类型
     */
    private String type;

    /**
     * 字段名称
     */
    private String fieldNameCn;

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

}
