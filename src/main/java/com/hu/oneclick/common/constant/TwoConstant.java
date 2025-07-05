package com.hu.oneclick.common.constant;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            case OneConstant.SCOPE.ONE_PROJECT:
                return OneConstant.SCOPE.ONE_PROJECT;
            default:
                throw new BizException(SysConstantEnum.SCOPE_ERROR.getCode(),SysConstantEnum.SCOPE_ERROR.getValue();
        }
    }


    /**
     * 裁剪子用户邮箱用户名
     * @param username
     * @return
     */
    public static String subUserNameCrop(String username){
        if (username.contains(OneConstant.COMMON.SUB_USER_SEPARATOR){
            int index = username.indexOf("#*&") + 3;
            return username.substring(index);
        }
        return username;
    }

    /**
     * list转字符串
     * @param strings
     * @return
     */
    public static String convertToString(List&lt;String> strings,Integer length){
        StringBuilder sb = new StringBuilder();

        for(int i = 0;i < strings.size(); i++){
            if (StringUtils.isEmpty(strings.get(i){
                sb.append(OneConstant.COMMON.REPLACE_EMPTY_CHARACTERS);
            }else {
                if (strings.get(i).length() > length) {
                    throw new BizException(SysConstantEnum.LENGTH_LIMIT_EXCEEDED.getCode(), SysConstantEnum.LENGTH_LIMIT_EXCEEDED.getValue();
                }
                sb.append(strings.get(i);
            }

            if (i == strings.size() - 1){
                break;
            }
            sb.append(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
        }
        return sb.toString();
    }

    /**
     * 字符串转list
     * @param str
     * @return
     */
    public static List&lt;String> convertToList(String str){
        if (StringUtils.isEmpty(str){
            return null;
        }
        List&lt;String> strings = Arrays.asList(str.split(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
        for (int i = 0; i < strings.size(); i++){
            if (strings.get(i).equals(OneConstant.COMMON.REPLACE_EMPTY_CHARACTERS){
                strings.set(i,"");
            }
        }
        return strings;
    }


    /**
     * 字符串转换成对象
     */
    public static <T> List&lt;T> convertToList(String source, Class<T> clazz){
        if(StringUtils.isEmpty(source){
            return new ArrayList&lt;>();
        }
        return JSON.parseArray(source, clazz);
    }

}