package com.hu.oneclick.common.exception;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.model.base.Resp;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qingyang
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Resp<String> handleException(Exception e, HttpServletRequest request){
        String msg=SysConstantEnum.SYSTEM_BUSY.getValue();
        if(e instanceof ValidException){
            msg= e.getMessage();
        }else if (e instanceof AccessDeniedException){
           msg = SysConstantEnum.NOT_PERMISSION.getValue();
        }else if (e instanceof BizException){
            return new Resp.Builder<String>().buildResult(((BizException) e).getCode(),e.getMessage());
        }else if (e instanceof HttpMessageNotReadableException || e instanceof NumberFormatException){
            msg = SysConstantEnum.PARAMETER_ABNORMAL.getValue();
        }
        return new Resp.Builder<String>().buildResult(msg);
    }
}
