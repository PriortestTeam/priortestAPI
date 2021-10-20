package com.hu.oneclick.common.enums;

import java.util.stream.Stream;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/9
 * @since JDK 1.8.0
 */
public enum OrderEnum {
    CREDIT_CARD(1, "信用卡"),
    WECHAT(2, "微信"),
    ZHIFUBAO(3, "支付宝"),
    BANKTRANSFER(4, "银行卡转账"),

    FIFTY(50, "50GB"),
    ONEHUNDRED(100, "100GB"),
    FIVEHUNDRED(500, "500GB"),
    TENTHOUSAND(1000, "1000GB"),

    MONTHLU(1, "月"),
    QUARTOLY(3, "季度"),
    HALFYEAR(6, "半年"),
    YEARLY(12, "一年");

    private Integer key;
    private String value;

    OrderEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
    public static OrderEnum toType(int key) {
        return Stream.of(OrderEnum.values())
                .filter(p -> p.key == key)
                .findAny()
                .orElse(null);
    }


}
