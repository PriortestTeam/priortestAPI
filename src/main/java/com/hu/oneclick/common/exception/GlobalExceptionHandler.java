package com.hu.oneclick.common.exception;

import com.hu.oneclick.model.base.Resp;
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
        String msg="系统繁忙："+ e.getMessage();
        if(e instanceof ValidException){
            msg=e.getMessage();
        }
        return new Resp.Builder<String>().buildResult(msg);
    }
}
