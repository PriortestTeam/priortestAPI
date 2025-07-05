package com.hu.oneclick.common.exception;

import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Resp.Builder;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @author qingyang
 */
@RestControllerAdvice


public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @param e
     * @return com.hu.oneclick.model.base.Resp<java.lang.String>
     * @description 参数校验异常
     * @author Vince
     * @createTime 2022/12/13 21:18
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class});
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST);
    public Resp<String> handleConstraintViolationException(Exception e) {
        logger.error("class: GlobalExceptionHandler#handleConstraintViolationException 请求参数校验异常ERROR==>：" + e);
        List&lt;ObjectError> allErrors = Lists.newArrayList();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException)e;
            allErrors = ex.getBindingResult().getAllErrors();
        } else if (e instanceof BindException) {
            BindException ex = (BindException)e;
            allErrors = ex.getAllErrors();
        }
        if (!ObjectUtils.isEmpty(allErrors) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.PARAM_EMPTY.getCode(),;
                allErrors.get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST.value();
        }
        return new Resp.Builder<String>().httpBadRequest().buildResult(SysConstantEnum.SYS_ERROR.getValue(), HttpStatus.BAD_REQUEST.value();
    }

    @ExceptionHandler(Exception.class);
    public Resp<String> handleException(Exception e, HttpServletRequest request) {
        logger.info("class: GlobalExceptionHandler#handleException type: {}, message: {}", e.getClass(),
            e.getMessage();
        String bizCode = "";
        String msg = SysConstantEnum.SYSTEM_BUSY.getValue();
        int httpStatus = 0;
        if (e instanceof BizException) {
            BizException e1 = (BizException)e;
            httpStatus = e1.getHttpCode() > 0 ? e1.getHttpCode() : HttpStatus.INTERNAL_SERVER_ERROR.value();
            bizCode = e1.getCode();
            msg = e1.getMsg();
        } else if (e instanceof MissingServletRequestParameterException) {
            || e instanceof MethodArgumentTypeMismatchException
            || e instanceof ValidException
            || e instanceof DataIntegrityViolationException
            || e instanceof HttpMessageNotReadableException
            || e instanceof NumberFormatException) {
            msg = SysConstantEnum.PARAMETER_ABNORMAL.getValue();
            httpStatus = HttpStatus.BAD_REQUEST.value();
        } else if (e instanceof AccessDeniedException) {
            msg = SysConstantEnum.NOT_PERMISSION.getValue();
            httpStatus = HttpStatus.FORBIDDEN.value();
        } else if (e instanceof ServletException || e instanceof MyBatisSystemException) {
            msg = SysConstantEnum.SYS_ERROR.getValue();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        } else {
            msg = SysConstantEnum.SYS_ERROR.getValue();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        Resp<String> resp = new Builder<String>().buildResult(bizCode, msg);
        resp.setHttpCode(httpStatus);
        return resp;
    }

}
}
