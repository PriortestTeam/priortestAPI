package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.domain.SysUser;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qingyang
 */
public class SubUserDto extends SysUser implements VerifyParam {

    private final String ALL = "ALL";

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 项目ids 字符传
     */
    private String projectIdStr;

    /**
     * 项目1 ； 项目2 。。。。
     */
    private String projectsSts;


    @Override
    public void verify() throws BizException {
        PasswordCheckerUtil passwordChecker = new PasswordCheckerUtil();
        if (StringUtils.isEmpty(super.getEmail())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(super.getPassword())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"密码" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(super.getUserName())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"用户名" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (!passwordChecker.check(super.getPassword())){
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue());
        } else if (!passwordChecker.check(roleId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"角色" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(projectIdStr)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }

    public String getALL() {
        return ALL;
    }

    public String getProjectsSts() {
        return projectsSts;
    }

    public void setProjectsSts(String projectsSts) {
        this.projectsSts = projectsSts;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getProjectIdStr() {
        return projectIdStr;
    }

    public void setProjectIdStr(String projectIdStr) {
        this.projectIdStr = projectIdStr;
    }
}
