package com.hu.oneclick.model.domain.dto;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
/**
 * @author qingyang
 */
@Data
@EqualsAndHashCode(callSuper=false)
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

    /** 默认打开项目
     * @Param: []
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/12/24
     */
    private String openProjectByDefaultId;

    private String openProjectByDefaultName;


    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(super.getEmail(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (StringUtils.isEmpty(super.getUserName(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"用户名" + SysConstantEnum.PARAM_EMPTY.getValue();
        }
        verifyPassword();
    }

    public void verifyPassword(){
        PasswordCheckerUtil passwordChecker = new PasswordCheckerUtil();
        if (StringUtils.isEmpty(super.getPassword(){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"密码" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (!passwordChecker.check(super.getPassword(){
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue();
        }
    }


    public final String getAll() {
        return "0";
    }

    public final String getDelimiter() {
        return ",";
    }

}
}
}
