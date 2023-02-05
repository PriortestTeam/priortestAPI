package com.hu.oneclick.common.enums;

/**
 * @author qingyang
 */
public enum  SysStatusEnum {

    PROJECT_STATUS_PROGRESS("3","开发中"),
    PROJECT_STATUS_PLAN("2","计划"),
    PROJECT_STATUS_CLOSED("1","关闭"),

    SPRINT_STATUS_CLOSED("2","关闭"),
    SPRINT_STATUS_OPEN("1","打开"),

    FEATURE_STATUS_PROGRESS("1","开发中"),
    FEATURE_STATUS_PLAN("2","计划"),
    FEATURE_STATUS_CLOSED("0","关闭"),

    TEST_CASE_LAST_RUN_STATUS_SUCCESS("1","成功"),
    TEST_CASE_LAST_RUN_STATUS_FILED("2","失败"),

    TEST_CYCLE_LAST_RUN_STATUS_SUCCESS("1","成功"),
    TEST_CYCLE_LAST_RUN_STATUS_FILED("2","失败"),

    TEST_CYCLE_STATUS_NOT_RUN("0","成功"),
    TEST_CYCLE_STATUS_COMPLETED("1","执行完成"),
    TEST_CYCLE_STATUS_UNCOMPLETED("2","未执行完成"),


    ISSUE_STATUS_OPEN("0","成功"),
    ISSUE_STATUS_ASSIGNED("0","成功"),
    ISSUE_STATUS_FIXED("0","成功"),


    TEST_CASE_STATUS_READY("Ready","待执行"),
    TEST_CASE_STATUS_DRAFT("Draft","草稿")

    ;


    private String key;
    private String value;

    SysStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
