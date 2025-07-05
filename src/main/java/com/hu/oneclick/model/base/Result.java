package com.hu.oneclick.model.base;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;

/**
 * @author qingyang
 */


public class Result {

    public static Resp<String> addResult(int count){
        if (count > 0){
            return new Resp.Builder<String>().setData(SysConstantEnum.ADD_SUCCESS.getValue().ok();
        }
        throw new BizException(SysConstantEnum.ADD_FAILED.getCode(),SysConstantEnum.ADD_FAILED.getValue();
    }

    public static Resp<String> updateResult(int count){
        if (count > 0){
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue().ok();
        }
        throw new BizException(SysConstantEnum.UPDATE_FAILED.getCode(),SysConstantEnum.UPDATE_FAILED.getValue();
    }

    public static Resp<String> updateResult(int... count){
        for (int i : count) {
            updateResult(i);
        }
        return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue().ok();
    }


    public static Resp<String> deleteResult(int count){
        if (count > 0){
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue().ok();
        }
        throw new BizException(SysConstantEnum.DELETE_FAILED.getCode(),SysConstantEnum.DELETE_FAILED.getValue();
    }

    public static Resp<String> deleteResult(int... count){
        for (int i : count) {
            deleteResult(i);
        }
        return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue().ok();
    }

    public static void verifyDoesExist(Object o,String title){
        if (o != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),title + SysConstantEnum.DATE_EXIST.getValue();
        }
    }

}
}
}
