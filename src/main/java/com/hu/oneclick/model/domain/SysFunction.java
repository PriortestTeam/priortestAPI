package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * sys_function
 * @author 
 */
@ApiModel(value="功能模块表")
@Data
public class  SysFunction implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 编号
     */
    @ApiModelProperty(value="编号")
    private String number;

    /**
     * 名称
     */
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 上级编号
     */
    @ApiModelProperty(value="上级编号")
    private String parentNumber;

    /**
     * 链接
     */
    @ApiModelProperty(value="链接")
    private String url;

    /**
     * 组件
     */
    @ApiModelProperty(value="组件")
    private String component;

    /**
     * 收缩
     */
    @ApiModelProperty(value="收缩")
    private Boolean state;

    /**
     * 排序
     */
    @ApiModelProperty(value="排序")
    private String sort;

    /**
     * 启用
     */
    @ApiModelProperty(value="启用")
    private Boolean enabled;

    /**
     * 类型
     */
    @ApiModelProperty(value="类型")
    private String type;

    /**
     * 功能按钮
     */
    @ApiModelProperty(value="功能按钮")
    private String pushBtn;

    /**
     * 图标
     */
    @ApiModelProperty(value="图标")
    private String icon;

    /**
     * 删除标记，0未删除，1删除
     */
    @ApiModelProperty(value="删除标记，0未删除，1删除")
    private String deleteFlag;

    private static final long serialVersionUID = 1L;
}