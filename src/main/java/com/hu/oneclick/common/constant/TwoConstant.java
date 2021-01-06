package com.hu.oneclick.common.constant;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;

/**
 * @author qingyang
 */
public class TwoConstant {

    public static String convertPermission(String scope){
        switch (scope){
            case OneConstant.SCOPE.ONE_SPRINT:
                return OneConstant.SCOPE.ONE_SPRINT;
            case OneConstant.SCOPE.ONE_FEATURE:
                return OneConstant.SCOPE.ONE_FEATURE;
            case OneConstant.SCOPE.ONE_TEST_CASE:
                return OneConstant.SCOPE.ONE_TEST_CASE;
            case OneConstant.SCOPE.ONE_TEST_CYCLE:
                return OneConstant.SCOPE.ONE_TEST_CYCLE;
            case OneConstant.SCOPE.ONE_ISSUE:
                return OneConstant.SCOPE.ONE_ISSUE;
            case OneConstant.SCOPE.ONE_SIGN_OFF:
                return OneConstant.SCOPE.ONE_SIGN_OFF;
            case OneConstant.SCOPE.ONE_SETTINGS:
                return OneConstant.SCOPE.ONE_SETTINGS;
            case OneConstant.SCOPE.ONE_DASHBOARD:
                return OneConstant.SCOPE.ONE_DASHBOARD;
            case OneConstant.SCOPE.ONE_REQUIREMENT:
                return OneConstant.SCOPE.ONE_REQUIREMENT;
            default:
                throw new BizException(SysConstantEnum.SCOPE_ERROR.getCode(),SysConstantEnum.SCOPE_ERROR.getValue());
        }
    }


    /**
     * 裁剪子用户邮箱用户名
     * @param username
     * @return
     */
    public static String subUserNameCrop(String username){
        if (username.contains(OneConstant.COMMON.SUB_USER_SEPARATOR)){
            int index = username.indexOf("#*&") + 3;
            return username.substring(index);
        }
        return username;
    }

}
