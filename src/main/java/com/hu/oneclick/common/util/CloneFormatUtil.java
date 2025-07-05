package com.hu.oneclick.common.util;

/**
 * @ClassName CloneFormatUtil
 * @Description 克隆业务格式工具类
 * @Author tom
 * @Date 4:09 PM 2024/5/28
 * @Version 1.0
 **/


public class CloneFormatUtil {

    /**
     * 克隆标题格式
     */
    public static final String CLONE_TITLE_FORMAT = "克隆%s";

    /**
     * 获取克隆标题
     *
     * @param title
     * @return
     */
    public static String getCloneTitle(String title) {
        return String.format(CLONE_TITLE_FORMAT, title);
    }
}
}
