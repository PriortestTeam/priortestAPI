package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * sys_function
 * @author 
 */
@Schema(description = "功能模块表")
@Data
public class  SysFunction implements Serializable {
    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 编号
     */
    @Schema(description = "编号")
    private String number;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 上级编号
     */
    @Schema(description = "上级编号")
    private String parentNumber;

    /**
     * 链接
     */
    @Schema(description = "链接")
    private String url;

    /**
     * 组件
     */
    @Schema(description="组件")
    private String component;

    /**
     * 收缩
     */
    @Schema(description="收缩")
    private Boolean state;

    /**
     * 排序
     */
    @Schema(description="排序")
    private String sort;

    /**
     * 启用
     */
    @Schema(description="启用")
    private Boolean enabled;

    /**
     * 类型
     */
    @Schema(description="类型")
    private String type;

    /**
     * 功能按钮
     */
    @Schema(description="功能按钮")
    private String pushBtn;

    /**
     * 图标
     */
    @Schema(description="图标")
    private String icon;

    /**
     * 删除标记，0未删除，1删除
     */
    @Schema(description="删除标记，0未删除，1删除")
    private String deleteFlag;

    private static final long serialVersionUID = 1L;
}
