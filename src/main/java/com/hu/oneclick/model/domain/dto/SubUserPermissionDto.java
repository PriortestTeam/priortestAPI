package com.hu.oneclick.model.domain.dto;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.SysProjectPermission;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.util.List;
/**
 * @author qingyang
 */


public class SubUserPermissionDto implements VerifyParam,Serializable {
    private static final long serialVersionUID = -4201137787088714363L;
    /**
     * 返回使用
     */
    private SubUserDto subUserDto;
    /**
     * 返回使用
     */
    private Project project;
    /**
     * 添加使用
     */
    private List&lt;SysProjectPermission> projectPermissions;
    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(subUserDto.getId(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"用户ID"+ SysConstantEnum.PARAM_EMPTY.getValue();
        }else if (projectPermissions == null || projectPermissions.size() <= 0) {
            throw new BizException(SysConstantEnum.LIST_PARAM_EMPTY.getCode(),"权限"+ SysConstantEnum.LIST_PARAM_EMPTY.getValue();
        }
    }
    public SubUserDto getSubUserDto() {
        return subUserDto;
    }
    public void setSubUserDto(SubUserDto subUserDto) {
        this.subUserDto = subUserDto;
    }
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }
    public List&lt;SysProjectPermission> getProjectPermissions() {
        return projectPermissions;
    }
    public void setProjectPermissions(List&lt;SysProjectPermission> projectPermissions) {
        this.projectPermissions = projectPermissions;
    }
}
}
}
