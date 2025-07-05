package com.hu.oneclick.common.util;
/**
 * @author qingyang
 */
public class NumberUtil {


    /**
     * 随机生成6位数字
     */
    public static String getVerifyCode() {
        int newNum = (int) ((Math.random() * 9 + 1) * 100000);
        return String.valueOf(newNum);
    }


    /**
     * 随机主用户识别码
     */
    public static String getIdentifier() {
        int newNum = (int) ((Math.random() * 9 + 1) * 10000000);
        return String.valueOf(newNum);
    }

}
