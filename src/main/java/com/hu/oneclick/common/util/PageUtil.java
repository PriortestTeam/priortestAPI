package com.hu.oneclick.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.PageDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分页工具类
 *
 * @author xiaohai
 * @date 2023/03/06
 */
public class PageUtil extends PageHelper {

    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = PageDomain.getPageDomain();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

    public static <E, T> PageInfo<T> convertPageInfo(List<E> list, Class<T> tClass) {
        List<T> dto = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            dto = list.stream().map(l -> BeanUtil.copyProperties(l, tClass)).collect(Collectors.toList());
        }
        PageInfo<E> of = PageInfo.of(list);
        PageInfo<T> of1 = PageInfo.of(dto);
        BeanUtil.copyProperties(of, of1, "list");
        return of1;
    }

}
