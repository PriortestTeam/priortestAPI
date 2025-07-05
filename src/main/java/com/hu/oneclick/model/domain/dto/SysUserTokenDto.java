package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/10
 * @since JDK 1.8.0
 */
@Data


public class SysUserTokenDto {
    private Date expirationTime;
    private String tokenName;
}
}
}
