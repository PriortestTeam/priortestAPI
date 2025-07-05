package com.hu.oneclick.model.entity;
import lombok.Data;
import java.io.Serializable;
/**
 * 角色权限
 *
 * @author WangYiCheng
 * @since 2022-10-31 20:58:43
 */
@Data


public class RoleFunction implements Serializable {
    private static final long serialVersionUID = 418948698502600249L;
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 角色Id
     */
    private Integer roleId;
    /**
     * 虚拟空间Id
     */
    private Integer roomId;
    /**
     * 默认勾选的权限Id
     */
    private String checkFunctionId;
    /**
     * 默认不可见权限Id
     */
    private String invisibleFunctionId;
}
}
}
