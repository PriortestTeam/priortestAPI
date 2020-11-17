package com.hu.oneclick.common.constant;

/**
 * @author qingyang
 */
public interface OneConstant {

    interface USER_TYPE {
        Integer admin = 1;
        Integer trail_user = 2;
        Integer testers = 3;
        Integer developer = 4;
    }
    interface ACTIVE_STATUS {
        //试用中
        Integer trial = 1;
        //激活成功
        Integer active_success = 2;
        //试用过期
        Integer trial_expired = 3;
        //激活失败
        Integer active_failed = 4;
    }


}
