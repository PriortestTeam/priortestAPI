package com.hu.oneclick.common.constant;

/**
 * @author qingyang
 */
public interface OneConstant {

    /**
     * 公共参数
     */
    interface COMMON {
        //成员用户分隔符
        String SUB_USER_SEPARATOR = "#*&";

        //数组转字符串分隔符
        String ARRAY_CONVERTER_STRING_DELIMITER = ",,,";

        //代替空字符
        String REPLACE_EMPTY_CHARACTERS = "{==}";

        //view 父子关联层级
        Integer VIEW_PARENT_CHILDREN_LEVEL = 3;
    }


    /**
     * 系统平台用户类型
     */
    interface PLATEFORM_USER_TYPE {
        Integer ORDINARY = 0;
        Integer MANAGER = 1;
        Integer SUB_USER = 2;
    }

    /**
     * 用户类型
     */
    interface USER_TYPE {
        Integer ADMIN = 1;
        Integer SUB_USER = 2;
    }

    /**
     * 激活状态
     */
    interface ACTIVE_STATUS {
        //试用中
        Integer TRIAL = 1;
        //激活成功
        Integer ACTIVE_SUCCESS = 2;
        //试用过期
        Integer TRIAL_EXPIRED = 3;
        //激活失败
        Integer ACTIVE_FAILED = 4;
        //待激活
        Integer ACTIVE_GENERATION = 5;
        //付费用户
        Integer PAYING_USERS = 6;
    }

    /**
     * redis key 前缀
     */
    interface REDIS_KEY_PREFIX {
        String LOGIN = "login_";

        String REGISTRY = "register_send_email";
        String MODIFY_PASSWORD = "modify_password_send_email";
        String RESET_PASSWORD = "reset_password_send_email";

        String SYS_CUSTOM_FIELDS = "sys_custom_fields";

        String viewScopeDown = "VIEW_SCOPE_DOWN";
    }

    /**
     * 密码
     */
    interface PASSWORD {
        /**
         * 激活账号
         */
        String ACTIVATION = "ACTIVATION";

        /**
         * 忘记密码
         */
        String FORGETPASSWORD = "FORGETPASSWORD";
        /**
         * 申请延期
         */
        String APPLY_FOR_AN_EXTENSION = "APPLY_FOR_AN_EXTENSION";
    }


    /**
     * 权限标识
     */
    interface PERMISSION {
        String ONE_FEATURE = "Feature";
        String ONE_SETTINGS = "Settings";
        String ONE_SPRINT = "Sprint";
        String ONE_SIGN_OFF = "Sign Off";
        String ONE_REQUIREMENT = "Requirement";
        String ONE_ISSUE = "Issue";
        String ONE_TEST_CYCLE = "TestCycle";
        String ONE_TEST_CASE = "TestCase";
        String ONE_DASHBOARD = "Dashboard";
        String ADD = "Add";
        String VIEW = "View";
        String DELETE = "Delete";
        String EDIT = "Edit";
        String BATCH_EDIT = "Batch Edit";
        String BATCH_DELETE = "Batch Delete";
        String VIEW_HISTORY = "View History";
        String IMPORT_TCS = "Import Tcs";
        String EXPORT_TCS = "Export Tcs";
        String CREATE_A_TES_CYCLE = "Create a Tes Cycle";
        String PUT_INTO_TEST_CYCLE = "Put into Test Cycle";
        String ADD_TEST_CASE = "Add Test Case";
        String RUN = "Run";
        String REMOVE_TESTCASE = "Remove TestCase";
        String STATUS_UPDATE = "Status Update";
        String ACCOUNT = "Account";
        String PERMISSION = "Permission";
        String PROJECT = "Project";
        String INTEGRATION = "Integration";
        String CUSTOM_FIELD = "Custom Field";
        String ISSUE_WORKFLOW = "Issue workflow";
        String MY_ACCOUNT = "My account";
    }

    /**
     * scope 作用范围，对应 PERMISSION 结构中的父节点
     */
    interface SCOPE {
        String ONE_PROJECT = "Project";
        String ONE_FEATURE = "Feature";
        String ONE_SETTINGS = "Settings";
        String ONE_SPRINT = "Sprint";
        String ONE_SIGN_OFF = "Sign Off";
        String ONE_REQUIREMENT = "Requirement";
        String ONE_ISSUE = "Issue";
        String ONE_TEST_CYCLE = "TestCycle";
        String ONE_TEST_CASE = "TestCase";
        String ONE_DASHBOARD = "Dashboard";
    }

    /**
     * 自定义字段类型
     */
    interface CUSTOM_FIELD_TYPE {
        /** masiyi 于备注
         * @Param:
         * @return:
         * @Author: MaSiyi
         * @Date: 2021/12/22
         */
        String RADIO = "radio";//单选
        String TEXT = "text";//文本
        String RICH_TEXT = "RichText";//备注
        String DROP_DOWN = "DropDown";//下拉框
        String CHECK_BOX = "CheckBox";//复选框
    }

    /**
     * email
     */
    interface EMAIL {
        String TITLE_IMPORTTESTCASE = "导入测试用例结果提醒";
        String TEMPLATEHTMLNAME_IMPORTTESTCASE = "importTestCase.html";
    }

    interface SystemConfigGroup {
        String REFERENCETIME = "ReferenceTime";
        String REFERENCEPERSONNO = "ReferencePersonNo";
        String SYSTEMCONFIG = "SystemConfig";
        String SPECICALDISCOUNT = "SpecicalDiscount";
        String DATASTRORAGE = "DataStrorage";
        String FLASHDISCOUNT = "FlashDiscount";
        String PAYMENTTYPE = "PaymentType";
        String APICALL = "APICall";
        String VIP = "VIP";
    }

    interface SystemConfigStatus {
        String ON = "0";
        String OFF = "1";

    }

    interface AREA_TYPE {

        String SIGNOFFSIGN = "SignOffSign";
    }
}
