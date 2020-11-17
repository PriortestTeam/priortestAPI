package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.VerifyParam;
import com.hu.oneclick.model.domain.SysUser;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author qingyang
 */
public class RegisterUser extends SysUser implements VerifyParam {

    private String emailCode;

    @Override
    public void verify() throws BizException {
        PasswordChecker passwordChecker = new PasswordChecker();
        if (StringUtils.isEmpty(super.getEmail())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(super.getPassword())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"密码" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(super.getUserName())){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"用户名" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(emailCode)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"邮箱验证码" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (!passwordChecker.check(super.getPassword())){
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue());
        }
    }


    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }


    /**
     * 密码规则
     */
    private class PasswordChecker {
        private boolean upperCase = false; // 包含大写字母
        private boolean lowerCase = false; // 包含小写字母
        private boolean letter = true; // 包含字母
        private boolean digit = true; // 包含数字
        private boolean special = true; // 包含特殊字符
        private Set<Character> specialCharSet; // 特殊字符集合
        private int minLength = 8; // 最小长度
        private int maxLength = 20; // 最大长度

        public PasswordChecker() {
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

            boolean containUpperCase = false;
            boolean containLowerCase = false;
            boolean containLetter = false;
            boolean containDigit = false;
            boolean containSpecial = true;

            for (char ch : password.toCharArray()) {
                if (Character.isUpperCase(ch)) {
                    containUpperCase = true;
                    containLetter = true;
                } else if (Character.isLowerCase(ch)) {
                    containLowerCase = true;
                    containLetter = true;
                } else if (Character.isDigit(ch)) {
                    containDigit = true;
                } else if (this.specialCharSet.contains(ch)) {
                    containSpecial = false;
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
//            specialChars.add(Character.valueOf('~'));
//            specialChars.add(Character.valueOf('`'));
//            specialChars.add(Character.valueOf('!'));
//            specialChars.add(Character.valueOf('@'));
//            specialChars.add(Character.valueOf('#'));
//            specialChars.add(Character.valueOf('$'));
//            specialChars.add(Character.valueOf('%'));
//            specialChars.add(Character.valueOf('^'));
//            specialChars.add(Character.valueOf('&'));
//            specialChars.add(Character.valueOf('*'));
//            specialChars.add(Character.valueOf('('));
//            specialChars.add(Character.valueOf(')'));
//            specialChars.add('-');
//            specialChars.add('_');
//            specialChars.add(Character.valueOf('+'));
//            specialChars.add(Character.valueOf('='));
//            specialChars.add(Character.valueOf('{'));
//            specialChars.add(Character.valueOf('['));
//            specialChars.add(Character.valueOf('}'));
//            specialChars.add(Character.valueOf(']'));
//            specialChars.add(Character.valueOf('|'));
//            specialChars.add(Character.valueOf('\\'));
//            specialChars.add(Character.valueOf(':'));
//            specialChars.add(Character.valueOf(';'));
//            specialChars.add(Character.valueOf('"'));
//            specialChars.add(Character.valueOf('\''));
//            specialChars.add(Character.valueOf('<'));
//            specialChars.add(Character.valueOf(','));
//            specialChars.add(Character.valueOf('>'));
//            specialChars.add(Character.valueOf('.'));
//            specialChars.add(Character.valueOf('?'));
//            specialChars.add(Character.valueOf('/'));
            return specialChars;
        }
    }
}
