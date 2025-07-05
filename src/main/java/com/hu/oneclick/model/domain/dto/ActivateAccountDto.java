package com.hu.oneclick.model.domain.dto;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/9/30
 * @since JDK 1.8.0
 */



public class ActivateAccountDto {
    public String email;
    public String password;
    public String rePassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }
}
}
}
