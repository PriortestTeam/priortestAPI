package com.hu.oneclick.common.constant;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/17
 * @since JDK 1.8.0
 */
public interface FieldConstant {

    //scope
    String PROJECT = "project";
    String FEATURE = "feature";
    String TESTCYCLE = "testCycle";
    String TESTCASE = "testCase";
    String ISSUE = "issue";

    //type
    interface type {
        /**
         * 单选
         */
        String RADIO = "radio";
        /**
         * 下拉
         */
        String DROPDOWN = "DropDown";
        /**
         * 文本
         */
        String TEXT = "text";
        /**
         * 备注
         */
        String MEMO = "memo";

    }
}
