package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * sys_function
 * @author 
 */
@Schemavalue="功能模块表"
@Data
public class  SysFunction implements Serializable {
    /**
     * 主键
     */
    @Schemavalue="主键"
    private Long id;

    /**
     * 编号
     */
    @Schemavalue="编号"
    private String number;

    /**
     * 名称
     */
    @Schemavalue="名称"
    private String name;

    /**
     * 上级编号
     */
    @Schemavalue="上级编号"
    private String parentNumber;

    /**
     * 链接
     */
    @Schemavalue="链接"
    private String url;

    /**
     * 组件
     */
    @Schemavalue="组件"
    private String component;

    /**
     * 收缩
     */
    @Schemavalue="收缩"
    private Boolean state;

    /**
     * 排序
     */
    @Schemavalue="排序"
    private String sort;

    /**
     * 启用
     */
    @Schemavalue="启用"
    private Boolean enabled;

    /**
     * 类型
     */
    @Schemavalue="类型"
    private String type;

    /**
     * 功能按钮
     */
    @Schemavalue="功能按钮"
    private String pushBtn;

    /**
     * 图标
     */
    @Schemavalue="图标"
    private String icon;

    /**
     * 删除标记，0未删除，1删除
     */
    @Schemavalue="删除标记，0未删除，1删除"
    private String deleteFlag;

    private static final long serialVersionUID = 1L;
}
