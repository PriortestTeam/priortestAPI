package com.hu.oneclick.common.exception;
/**
 * @author qingyang
 */


public class BaseException extends RuntimeException {
    protected String code;
    protected String msg;
    protected int httpCode;
    public BaseException() {
        super();
    }
    public BaseException(String message) {
        super(message);
        this.msg = message;
    }
    public BaseException(String code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public int getHttpCode() {
        return httpCode;
    }
    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
}
}
