package com.hu.oneclick.relation.enums;

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
    TEST_CASE_TO_STEP("测试用例与步骤关系", "TEST_CASE_TO_STEP");

    private final String name;
    private final String value;

    RelationCategoryEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

}
