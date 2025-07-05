package com.hu.oneclick.common.exception;
/**
 * @author qingyang
 */

public class BizException extends BaseException {
    public BizException() {
        super();
    }
    public BizException(String message) {
        super(message);
        this.msg = message;
    }
    public BizException(String code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }
    public BizException(String code, String msg, int httpCode) {
        super(msg);
        this.msg = msg;
        this.code = code;
        this.httpCode = httpCode;
    }
}
}
}
