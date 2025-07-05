package com.hu.oneclick.common.exception;
/**
 * @author qingyang
 */


public class ValidException extends BaseException{
    public ValidException() {
        super();
    }
    public ValidException(String message) {
        super(message);
        this.msg=message;
    }
    public ValidException(String code, String msg) {
        super();
        this.msg=msg;
        this.code=code;
    }
}
}
}
