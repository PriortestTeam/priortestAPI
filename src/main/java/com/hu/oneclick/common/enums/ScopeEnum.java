package com.hu.oneclick.common.enums;

/**
 * 范围 选项
 *
 * @author xiaohai
 * @date 2023/08/25
 */
public enum ScopeEnum {
    // 项目
    PROJECT("1000001", "project", "Project","com.hu.oneclick.model.domain.Project","projectServiceImpl"),
    // 故事
    FEATURE("2000001", "feature", "Feature","com.hu.oneclick.model.domain.Feature","featureServiceImpl"),
    // 测试用例
    TEST_CASE("3000001", "testCase",  "TestCase", "com.hu.oneclick.model.domain.TestCase", "testCaseServiceImpl"),
    // 测试周期
    TEST_CYCLE("5000001", "testCycle",  "TestCycle", "com.hu.oneclick.model.domain.TestCycle", "testCycleServiceImpl"),
    // 缺陷
    ISSUE("7000001", "issue",  "Issue", "com.hu.oneclick.model.domain.Issue", "issueServiceImpl"),
    // 迭代
    SPRINT("8000001", "sprint",  "Sprint", "com.hu.oneclick.model.domain.Sprint", "sprintServiceImpl"),
    ;

    private String code;
    private String name;
    private String bean;
    private String beanPath;
    private String service;

    ScopeEnum() {
    }

    ScopeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    ScopeEnum(String code, String name, String bean, String beanPath, String service) {
        this.code = code;
        this.name = name;
        this.bean = bean;
        this.beanPath = beanPath;
        this.service = service;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getBeanPath() {
        return beanPath;
    }

    public void setBeanPath(String beanPath) {
        this.beanPath = beanPath;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public static ScopeEnum getByCode(String code) {
        for (ScopeEnum value : ScopeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static ScopeEnum getByName(String name) {
        for (ScopeEnum value : ScopeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
