package com.hu.oneclick.common.util;

import cn.hutool.core.bean.BeanUtil;

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
    
    /**
     * 克隆对象
     *
     * @param source 源对象
     * @param targetClass 目标类型
     * @return 克隆的对象
     */
    public static <T> T cloneObject(Object source, Class<T> targetClass) {
        return BeanUtil.copyProperties(source, targetClass);
    }ing.format(CLONE_TITLE_FORMAT, title);
    }
}
