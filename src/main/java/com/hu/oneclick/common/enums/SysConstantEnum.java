package com.hu.oneclick.common.enums;

/**
 * @author qingyang
 */
public enum SysConstantEnum {

    FAILED("-1","调用失败。"),
    SUCCESS(SysConstantEnum.NUMBER,"调用成功。"),
    SYS_ERROR("-1","系统异常。"),
    PARAM_EMPTY( SysConstantEnum.NUMBER + "001","不能为空。"),
    PASSWORD_RULES(SysConstantEnum.NUMBER + "001","密码由(-、_数字、小写、大写字母)，最小8位，最大20位字符组成。"),
    REGISTER_SUCCESS(SysConstantEnum.NUMBER,"注册成功。"),
    REGISTER_FAILED(SysConstantEnum.NUMBER + "011","注册失败。"),
    LOGIN_SUCCESS(SysConstantEnum.NUMBER,"登录成功。"),
    LOGIN_FAILED(SysConstantEnum.NUMBER + "011","用户名或密码错误。"),
    USERNAME_ERROR(SysConstantEnum.NUMBER + "011","用户名异常。"),
    AUTH_FAILED(SysConstantEnum.NUMBER + "011","认证失败，请重新登录。"),
    NO_DUPLICATE_REGISTER(SysConstantEnum.NUMBER + "011","不可重复注册。"),
    VERIFY_CODE_ERROR(SysConstantEnum.NUMBER + "012","验证码错误。"),
    PLEASE_TRY_AGAIN_LATER(SysConstantEnum.NUMBER + "013","请稍后再试。");








    private final static String NUMBER = "200";
    private String value;
    private String code;

    SysConstantEnum(String code, String value) {
        this.value = value;
        this.code = code;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
