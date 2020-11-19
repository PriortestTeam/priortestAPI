package com.hu.oneclick.common.constant;

/**
 * @author qingyang
 */
public interface OneConstant {

    interface USER_TYPE {
        Integer ADMIN = 1;
        Integer TRAIL_USER = 2;
        Integer TESTERS = 3;
        Integer DEVELOPER = 4;
    }
    interface ACTIVE_STATUS {
        //试用中
        Integer TRIAL = 1;
        //激活成功
        Integer ACTIVE_SUCCESS = 2;
        //试用过期
        Integer TRIAL_EXPIRED = 3;
        //激活失败
        Integer ACTIVE_FAILED = 4;
    }

    interface REDIS_KEY_PREFIX {
        String LOGIN = "login_";

        String REGISTRY = "register_send_email";
        String MODIFY_PASSWORD = "modify_password_send_email";
        String RESET_PASSWORD = "reset_password_send_email";
    }


}
