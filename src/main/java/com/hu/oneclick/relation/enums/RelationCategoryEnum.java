package com.hu.oneclick.relation.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 关系分类枚举
 *
 * @author xiaohai
 * @date 2023/06/05
 */
@Getter
@SwaggerDisplayEnum
public enum RelationCategoryEnum {

    /** 测试用例与步骤关系 */
    TEST_CASE_TO_STEP("测试用例与步骤关系", "TEST_CASE_TO_STEP"),
    /** 测试用例与迭代关系 */
    TEST_CASE_TO_SPRINT("测试用例与迭代关系", "TEST_CASE_TO_SPRINT"),
    /** 测试用例与故事关系 */
    TEST_CASE_TO_FEATURE("测试用例与故事关系", "TEST_CASE_TO_FEATURE"),
    /** 测试用例与缺陷关系 */
    TEST_CASE_TO_ISSUE("测试用例与缺陷关系", "TEST_CASE_TO_ISSUE"),
    /** 测试周期与故事关系 */
    TEST_CYCLE_TO_FEATURE("测试周期与故事关系", "TEST_CYCLE_TO_FEATURE"),
    /** 测试周期与迭代关系 */
    TEST_CYCLE_SPRINT("测试周期与迭代关系", "TEST_CYCLE_SPRINT"),
    /** 缺陷与故事关系 */
    ISSUE_FEATURE("缺陷与故事关系", "ISSUE_FEATURE"),
    /** 缺陷与迭代关系 */
    ISSUE_SPRINT("缺陷与迭代关系", "ISSUE_SPRINT"),
    /** 缺陷与测试用例关系 */
    ISSUE_TEST_CASE("缺陷与测试用例关系", "ISSUE_TEST_CASE"),
    ;

    private final String name;
    private final String value;

    RelationCategoryEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return StrUtil.format("{}[{}]", value, name);
    }

}
