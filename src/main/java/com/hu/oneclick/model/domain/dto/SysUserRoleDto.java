package com.hu.oneclick.model.domain.dto;
import lombok.Data;
import java.io.Serializable;
/**
 * SysUserRoleDto
 *
 * @Param:
 * @return:
 * @Author: MaSiyi
 * @Date: 2022/1/3
 */
@Data
public class SysUserRoleDto implements Serializable {
    private static final long serialVersionUID = -67242842971294342L;
    /**
     * userId
     *
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2022/1/3
     */
    private String id;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 角色Id
     */
    private String roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色描述
     */
    private String roleDesc;
    private String roomId;
}
}
}
