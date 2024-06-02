package com.hu.oneclick.common.enums;

/**
 * @author qingyang
 */
public enum SysConstantEnum {

    //失败
    SYSTEM_BUSY("-1","系统繁忙。"),
    FAILED("-1","调用失败。"),
    ADD_FAILED("-1","新增失败。"),
    UPLOAD_FILE_FAILED("-1","上传文件失败。"),
    UPDATE_FAILED("-1","更新失败。"),
    DELETE_FAILED("-1","删除失败。"),
    SYS_ERROR("-1","系统异常。"),

    //业务失败
    PARAM_EMPTY( SysConstantEnum.NUMBER + "001","不能为空。"),
    LIST_PARAM_EMPTY( SysConstantEnum.NUMBER + "001","数组必须有至少一个值。"),
    LENGTH_LIMIT_EXCEEDED( SysConstantEnum.NUMBER + "001","超出长度限制。"),
    PASSWORD_RULES(SysConstantEnum.NUMBER + "001","密码由(数字、小写、大写字母、特殊字符！@#等)，最小8位，最大20位字符组成。"),
    CREATE_SUB_USER_SUCCESS(SysConstantEnum.NUMBER,"创建成员用户成功。请提醒成员去邮箱激活账号"),
    CREATE_PLATFORM_USER_SUCCESS(SysConstantEnum.NUMBER,"创建平台用户成功。"),
    REGISTER_FAILED(SysConstantEnum.NUMBER + "011","注册失败。"),
    CREATE_SUB_USER_FAILED(SysConstantEnum.NUMBER + "011","创建成员用户失败。"),
    CREATE_PLATFORM_USER_FAILED(SysConstantEnum.NUMBER + "011","创建平台用户失败。"),
    LOGIN_FAILED(SysConstantEnum.NUMBER + "011","用户名或密码错误。"),
    USERNAME_ERROR(SysConstantEnum.NUMBER + "011","用户名异常。"),
    MASTER_ACCOUNT_ERROR(SysConstantEnum.NUMBER + "011","主账号异常。"),
    SUB_USERNAME_ERROR(SysConstantEnum.NUMBER + "011","您的成员列表已存在该用户。"),
    AUTH_FAILED(SysConstantEnum.NUMBER + "011","认证失败，请重新登录。"),
    NO_DUPLICATE_REGISTER(SysConstantEnum.NUMBER + "011","不可重复注册。"),
    VERIFY_CODE_ERROR(SysConstantEnum.NUMBER + "012","验证码错误。"),
    PLEASE_TRY_AGAIN_LATER(SysConstantEnum.NUMBER + "013","请稍后再试。"),
    NOT_DETECTED_EMAIL(SysConstantEnum.NUMBER + "014","未检测到邮箱。"),
    NOT_PERMISSION(SysConstantEnum.NUMBER + "015","无权访问"),
    PARAMETER_ABNORMAL(SysConstantEnum.NUMBER + "016","参数异常。"),
    DATE_EXIST(SysConstantEnum.NUMBER + "017","已存在，不可重复添加。"),
    NOT_PROJECT(SysConstantEnum.NUMBER + "018","请选择一个项目。"),
    SCOPE_ERROR(SysConstantEnum.NUMBER + "019","scope 异常。"),
    REPASSWORD_ERROR(SysConstantEnum.NUMBER + "020","两次密码不一致"),
    NOUSER_ERROR(SysConstantEnum.NUMBER + "021","没有该用户"),
    HAS_BEEN_ACTIVATED_ONCE(SysConstantEnum.NUMBER + "022","激活失败"),
    UPDATE_FILE_OUT_COUNT(SysConstantEnum.NUMBER + "023", "上传文件数量过多"),
    VIEW_ONN_FILTER_IS_REDUNDANT(SysConstantEnum.NUMBER + "030","过滤项不可重复。"),
    LINKSTRERROR(SysConstantEnum.NUMBER + "031","链接错误。"),
    DATE_EXIST_TITLE(SysConstantEnum.NUMBER + "032","周期名词已存在"),
    DATA_NOT_FOUND("404", "查无记录"),



    //成功
    SUCCESS(SysConstantEnum.NUMBER,"调用成功。"),
    ADD_SUCCESS(SysConstantEnum.NUMBER,"新增成功。"),
    UPDATE_SUCCESS(SysConstantEnum.NUMBER,"更新成功。"),
    DELETE_SUCCESS(SysConstantEnum.NUMBER,"删除成功。"),
    LOGIN_SUCCESS(SysConstantEnum.NUMBER,"登录成功。"),
    REGISTER_SUCCESS(SysConstantEnum.NUMBER,"注册成功。"),
    REREGISTER_SUCCESS(SysConstantEnum.NUMBER,"请重新进入邮箱激活。"),
    LOGOUT_SUCCESS(SysConstantEnum.NUMBER,"注销成功。"),
    ACTIVATION_SUCCESS(SysConstantEnum.NUMBER,"激活成功。"),


    //测试用例导入定义错误类型
    IMPORT_TESTCASE_ERROR_NOTSELECT("NOTSELECT","是属于下拉菜单的，导入数据必须与系统中已有数据相对应"),
    IMPORT_TESTCASE_ERROR_REQUIRED("REQUIRED","必填项未填写，导入数据必须填写此项"),
    IMPORT_TESTCASE_ERROR_NOFEATURE("NOFEATURE","未找到存在项目中Feature"),
    IMPORT_TESTCASE_ERROR_EXIST_FEATURE_EXTERNALID("EXIST_FEATURE_EXTERNALID","此Feature下已存在ExternaID无法进行新增"),

    // 测试周期相关
    TEST_CYCLE_NOT_EXIST_PROJECT("400", "项目 ID 不存在项目里"),
    TEST_CYCLE_NOT_INCLUDE_IN_PROJECT("400", "Test Cycle ID 不存在项目中"),
    TEST_CASE_PROJECT_ID_NOT_EXIST("400", "项目 ID 不统一"),
    TEST_CYCLE_NOT_MATE_PROJECT("400","当前项目ID不匹配"),
    TEST_CASE_NOT_EXIST("400", "测试用例不存在"),

    //version
    VERSION_NOT_MATCH("400", "修改版本不一致"),
    VERSION_HAVE_EXSIST("400", "版本已存在");



    private final static String NUMBER = "200";
    private String value;
    private String code;

    SysConstantEnum(String code, String value) {
        this.value = value;
        this.code = code;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
