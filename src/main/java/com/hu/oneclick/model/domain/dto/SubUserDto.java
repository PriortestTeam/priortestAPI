package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.domain.SysUser;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author qingyang
 */
public class SubUserDto extends SysUser implements VerifyParam, Serializable {

    private static final long serialVersionUID = 1993189923318507589L;

    /**
     * 角色名
     */
    private String roleName;

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
        if (StringUtils.isEmpty(super.getEmail())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(super.getUserName())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"用户名" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(super.getSysRoleId())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"角色" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(projectIdStr)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        verifyPassword();
    }

    public void verifyPassword(){
        PasswordCheckerUtil passwordChecker = new PasswordCheckerUtil();
        if (StringUtils.isEmpty(super.getPassword())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"密码" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (!passwordChecker.check(super.getPassword())){
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue());
        }
    }


    public final String getAll() {
        return "0";
    }

    public final String getDelimiter() {
        return ",";
    }

    public String getProjectsSts() {
        return projectsSts;
    }

    public void setProjectsSts(String projectsSts) {
        this.projectsSts = projectsSts;
    }

    public String getProjectIdStr() {
        return projectIdStr;
    }

    public void setProjectIdStr(String projectIdStr) {
        this.projectIdStr = projectIdStr;
    }



    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
