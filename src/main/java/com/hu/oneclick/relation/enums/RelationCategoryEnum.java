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
    /** 测试周期与用例关系 */
    TEST_CYCLE_TO_CASE("测试周期与用例关系", "TEST_CYCLE_TO_CASE"),
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
