package com.hu.oneclick.model.domain.dto;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
/**
 * @author qingyang
 */


public class RegisterUser extends SysUser implements VerifyParam {
    private String emailCode;
    @Override
    public void verify() throws BizException {
        PasswordCheckerUtil passwordChecker = new PasswordCheckerUtil();
        if (StringUtils.isEmpty(super.getEmail(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (StringUtils.isEmpty(super.getPassword(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"密码" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (StringUtils.isEmpty(super.getUserName(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"用户名" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (StringUtils.isEmpty(emailCode){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱验证码" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (!passwordChecker.check(super.getPassword(){
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue();
        }
    }
    public String getEmailCode() {
        return emailCode;
    }
    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
}
}
