package com.hu.oneclick.common.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * @author xwf
 * @date 2021/10/18 20:55
 * 使用jasypt对配置文件进行加密的工具
 */
public class JasyptUtil {

    public static void main(String[] args) {
        //加密的秘钥
        String encPassword = "onclick";
        //要加密的密码
        String password = "XYJGZXQGSDHZFGLI";
        StandardPBEStringEncryptor se = new StandardPBEStringEncryptor();
        se.setPassword(encPassword);
        String postgres = se.encrypt(password);
        System.out.println("加密后的密码为："+postgres);
    }
}
