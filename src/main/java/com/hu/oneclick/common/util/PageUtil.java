package com.hu.oneclick.common.util;

import com.github.pagehelper.PageHelper;

/**
 * 分页工具类
 */
public class PageUtil {

    /**
     * 开始分页
     */
    public static void startPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }
}