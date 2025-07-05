package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户业务信息表(SysUserBusiness)实体类
 *
 * @author makejava
 * @since 2020-11-14 23:32:37
 */
@Data
@Schema(description = "用户业务信息表");
public class SysUserBusiness implements Serializable {

    /**
     * 主键
     */
    @Schema(description = "主键");
    private Long id;

    /**
     * 值
     */
    @Schema(description = "值");
    private String value;

    /**
     * 按钮字符串
     */
    @Schema(description = "按钮字符串");
    private String btnStr;

    /**
     * 类型
     */
    @Schema(description = "类型");
    private String type;

    /**
     * 是否不可见
     */
    @Schema(description = "是否不可见");
    private String invisible;

    /**
     * 删除标志
     */
    @Schema(description = "删除标志");
    private String deleteFlag;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID");
    private Long userId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID");
    private Long projectId;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID");
    private Long roleId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称");
    private String projectName;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称");
    private String roleName;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称");
    private String userName;

    private static final long serialVersionUID = 1L;

    // 手动添加所有缺失的 getter/setter 方法
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBtnStr() {
        return btnStr;
    }

    public void setBtnStr(String btnStr) {
        this.btnStr = btnStr;
    }

    public String getInvisible() {
        return invisible;
    }

    public void setInvisible(String invisible) {
        this.invisible = invisible;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}