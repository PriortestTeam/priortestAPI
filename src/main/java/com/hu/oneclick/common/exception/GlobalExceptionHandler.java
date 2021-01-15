package com.hu.oneclick.common.exception;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.impl.CustomFieldServiceImpl;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qingyang
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Resp<String> handleException(Exception e, HttpServletRequest request){
        String msg=SysConstantEnum.SYSTEM_BUSY.getValue();
        if(e instanceof ValidException
                || e instanceof DataIntegrityViolationException
                || e instanceof ServletException
                || e instanceof MyBatisSystemException){
            msg= e.getMessage();
        }else if (e instanceof AccessDeniedException){
           msg = SysConstantEnum.NOT_PERMISSION.getValue();
        }else if (e instanceof BizException){
            return new Resp.Builder<String>().buildResult(((BizException) e).getCode(),e.getMessage());
        }else if (e instanceof HttpMessageNotReadableException || e instanceof NumberFormatException){
            msg = SysConstantEnum.PARAMETER_ABNORMAL.getValue();
        }
        logger.info("class: GlobalExceptionHandler#handleException error[]"+e.getMessage());
        return new Resp.Builder<String>().buildResult(msg);
    }
}
