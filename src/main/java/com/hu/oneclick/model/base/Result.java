package com.hu.oneclick.model.base;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;

/**
 * @author qingyang
 */
public class Result {

    public static Resp<String> addResult(int count){
        if (count > 0){
            return new Resp.Builder<String>().setData(SysConstantEnum.ADD_SUCCESS.getValue()).ok();
        }
        return new Resp.Builder<String>().setData(SysConstantEnum.ADD_FAILED.getValue()).fail();
    }

    public static Resp<String> updateResult(int count){
        if (count > 0){
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
        }
        return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_FAILED.getValue()).fail();
    }

    public static Resp<String> deleteResult(int count){
        if (count > 0){
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_FAILED.getValue()).ok();
        }
        return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_FAILED.getValue()).fail();
    }

    public static void verifyDoesExist(Object o){
        if (o != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),SysConstantEnum.DATE_EXIST.getValue());
        }
    }

}
