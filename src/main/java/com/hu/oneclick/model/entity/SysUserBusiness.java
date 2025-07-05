package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * sys_user_business
 * @author 
 */
@Schema(description="用户角色模块关系表")
@Data
public class SysUserBusiness implements Serializable {
    /**
     * 主键
     */
    @Schema(description="主键")
    private Long id;

    /**
     * 类别
     */
    @Schema(description="类别")
    private String type;

    /**
     * 主id
     */
    @Schema(description="主id")
    private String keyId;

    /**
     * 值
     */
    @Schema(description="值")
    private String value;

    /**
     * 值
     */
    @Schema(description="不可见项")
    private String invisible;

    /**
     * 按钮权限
     */
    @Schema(description="按钮权限")
    private String btnStr;

    /**
     * 租户id
     */
    @Schema(description="租户id")
    private Long tenantId;

    /**
     * 删除标记，0未删除，1删除
     */
    @Schema(description="删除标记，0未删除，1删除")
    private String deleteFlag;

    @Schema(description = "角色id")
    private Long  roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "项目id")
    private Long projectId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    private static final long serialVersionUID = 1L;
}
package com.hu.oneclick.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserBusiness {
    
    private Long id;
    
    private String value;
    
    private String btnStr;
    
    private String type;
    
    private Boolean invisible;
    
    public String getValue() {
        return value;
    }
    
    public String getBtnStr() {
        return btnStr;
    }
    
    public Boolean getInvisible() {
        return invisible;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
