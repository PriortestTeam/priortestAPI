package com.hu.oneclick.relation.enums;

import lombok.Getter;

/**
 * 关系分类枚举
 *
 * @author xiaohai
 * @date 2023/06/05
 */
@Getter
public enum RelationCategoryEnum {

    /** 测试用例与步骤关系 */
    TEST_CASE_TO_STEP("TEST_CASE_TO_STEP");

    private final String value;

    RelationCategoryEnum(String value) {
        this.value = value;
    }
}
