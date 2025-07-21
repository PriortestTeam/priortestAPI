package com.hu.oneclick.common.util;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.bean.BeanUtil;

import java.util.List;
import java.util.ArrayList;

/**
 * 分页工具类
 */
public class PageUtil {

    /**
     * 开始分页 - 无参数版本
     */
    public static void startPage() {
        // 默认分页参数
        PageHelper.startPage(1, 10);
    }

    /**
     * 开始分页
     */
    public static void startPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * 清除分页
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

    /**
     * 手动分页
     */
    public static <T> PageInfo<T> manualPaging(List<T> list) {
        return new PageInfo<>(list);
    }

    /**
     * 转换分页信息
     */
    public static <E, T> PageInfo<T> convertPageInfo(List<E> sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<>();
        for (E source : sourceList) {
            T target = BeanUtil.copyProperties(source, targetClass);
            targetList.add(target);
        }
        return new PageInfo<>(targetList);
    }
}