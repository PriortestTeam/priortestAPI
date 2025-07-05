package com.hu.oneclick.common.util;
/**
 * @author qingyang
 */
import java.util.LinkedHashSet;
import java.util.Set;
/**
 * 密码规则
 */


public class PasswordCheckerUtil {
    // 包含大写字母
    private boolean upperCase = true;
    // 包含小写字母
    private boolean lowerCase = true;
    // 包含字母
    private boolean letter = true;
    // 包含数字
    private boolean digit = true;
    // 包含特殊字符
    private boolean special = true;
    // 特殊字符集合
    private Set<Character> specialCharSet;
    // 最小长度
    private int minLength = 8;
    // 最大长度
    private int maxLength = 20;
    public PasswordCheckerUtil() {
        this.specialCharSet = defaultSpecialCharSet();
    }
    /**
     * 密码符合规则，返回true
     */
    public boolean check(String password) {
        if (password == null || password.length() < this.minLength || password.length() > this.maxLength) {
            // 长度不符合
            return false;
        }
        //包含大写
        boolean containUpperCase = false;
        //包含小写
        boolean containLowerCase = false;
        //包含字母
        boolean containLetter = false;
        //包含数字
        boolean containDigit = false;
        //包含特殊
        boolean containSpecial = false;
        for (char ch : password.toCharArray() {
            if (Character.isUpperCase(ch) {
                containUpperCase = true;
                containLetter = true;
            } else if (Character.isLowerCase(ch) {
                containLowerCase = true;
                containLetter = true;
            } else if (Character.isDigit(ch) {
                containDigit = true;
            } else if (this.specialCharSet.contains(ch) {
                containSpecial = true;
            } else {
                // 非法字符
                return false;
            }
        }
        if (this.upperCase && !containUpperCase) {
            return false;
        }
        if (this.lowerCase && !containLowerCase) {
            return false;
        }
        if (this.letter && !containLetter) {
            return false;
        }
        if (this.digit && !containDigit) {
            return false;
        }
        if (this.special && !containSpecial) {
            return false;
        }
        return true;
    }
    Set<Character> defaultSpecialCharSet() {
        Set<Character> specialChars = new LinkedHashSet<>();
        // 键盘上能找到的符号
        specialChars.add(Character.valueOf('~');
        specialChars.add(Character.valueOf('');
        specialChars.add(Character.valueOf('!');
        specialChars.add(Character.valueOf('@');
        specialChars.add(Character.valueOf('#');
        specialChars.add(Character.valueOf('$');
        specialChars.add(Character.valueOf('%');
        specialChars.add(Character.valueOf('^');
        specialChars.add(Character.valueOf('&');
        specialChars.add(Character.valueOf('*');
        specialChars.add(Character.valueOf('(');
        specialChars.add(Character.valueOf(')');
        specialChars.add('-');
        specialChars.add('_');
        specialChars.add(Character.valueOf('+');
        specialChars.add(Character.valueOf('=');
        specialChars.add(Character.valueOf('{');
        specialChars.add(Character.valueOf('[');
        specialChars.add(Character.valueOf('}');
        specialChars.add(Character.valueOf(']');
        specialChars.add(Character.valueOf('|');
        specialChars.add(Character.valueOf('\\');
        specialChars.add(Character.valueOf(':');
        specialChars.add(Character.valueOf(';');
        specialChars.add(Character.valueOf('"');
        specialChars.add(Character.valueOf('\'');
        specialChars.add(Character.valueOf('<');
        specialChars.add(Character.valueOf(',');
        specialChars.add(Character.valueOf('>');
        specialChars.add(Character.valueOf('.');
        specialChars.add(Character.valueOf('?');
        specialChars.add(Character.valueOf('/');
        return specialChars;
    }
}
}
}
