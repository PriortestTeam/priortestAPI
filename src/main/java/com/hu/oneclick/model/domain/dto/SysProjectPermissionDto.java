package com.hu.oneclick.model.domain.dto;
import com.hu.oneclick.model.entity.SysProjectPermission;
/**
 * @author qingyang
 */


public class SysProjectPermissionDto extends SysProjectPermission {
    /**
     * 权限标识符
     */
    private String markName;
    /**
     * 父id
     */
    private String parentId;
    public String getMarkName() {
        return markName;
    }
    public void setMarkName(String markName) {
        this.markName = markName;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
}
}
