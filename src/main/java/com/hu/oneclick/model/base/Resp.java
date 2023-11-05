package com.hu.oneclick.model.base;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.SysConstantEnum;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @author qingyang
 */
public class Resp<T> {

    private String code;
    private String msg;
    private Long total;
    private int httpCode;
    private T data;

    public Resp(){}

    public Resp(Builder<T> builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.data = builder.data;
        this.total = builder.total;
        this.httpCode = builder.httpCode;
    }

    public static class  Builder<T> {
        private String code;
        private String msg;
        private Long total;
        private int httpCode;
        private T data;


        public Builder() {
        }
        public Resp<T> buildResult(String code,String msg){
            this.code = code;
            this.msg = msg;
            return new Resp<T>(this);
        }
        public Resp<T> buildResult(String code,String msg,T data){
            this.code = code;
            this.msg = msg;
            this.data = data;
            return new Resp<T>(this);
        }
        public Resp<T> buildResult(String msg){
            this.code = SysConstantEnum.FAILED.getCode();
            this.msg = msg;
            return new Resp<T>(this);
        }
        public Resp<T> ok(){
            this.code= SysConstantEnum.SUCCESS.getCode();
            this.msg= SysConstantEnum.SUCCESS.getValue();
            this.httpCode = HttpStatus.OK.value();
            return new Resp<T>(this);
        }
        public Resp<T> fail(){
            this.code= SysConstantEnum.FAILED.getCode();
            this.msg= SysConstantEnum.FAILED.getValue();
            this.httpCode = HttpStatus.FORBIDDEN.value();
            return new Resp<T>(this);
        }
        public Builder<T> totalSize(Integer total){
            this.totalSize((long)total);
            return this;
        }
        public Builder<T> totalSize(Long total){
            this.total=total;
            return this;
        }

        public Builder<T> total(Object obj){
            List<T> list = (List<T>) obj;
            this.total =  new PageInfo(list).getTotal();
            return this;
        }
        public Builder<T> setData(T data){
            this.data=data;
            return this;
        }
        public Builder<T> httpOk(){
            this.httpCode = HttpStatus.OK.value();
            return this;
        }
        public Builder<T> httpDeny(){
            this.httpCode = HttpStatus.FORBIDDEN.value();
            return this;
        }
        public Builder<T> httpBadRequest(){
            this.httpCode = HttpStatus.BAD_REQUEST.value();
            return this;
        }
        public Builder<T> httpNotFound(){
            this.httpCode = HttpStatus.NOT_FOUND.value();
            return this;
        }
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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
