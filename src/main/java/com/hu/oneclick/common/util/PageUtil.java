package com.hu.oneclick.common.util;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.PageDomain;
import com.hu.oneclick.common.page.TableSupport;
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
    public static <E, T> PageInfo<T> convertPageInfo(List&lt;E> list, Class<T> tClass) {
        List&lt;T> dto = new ArrayList&lt;>();
        if (CollUtil.isNotEmpty(list) {
            dto = list.stream().map(l -> BeanUtil.copyProperties(l, tClass).collect(Collectors.toList();
        }
        PageInfo<E> of = PageInfo.of(list);
        PageInfo<T> of1 = PageInfo.of(dto);
        BeanUtil.copyProperties(of, of1, "list");
        return of1;
    }
    public static <E> PageInfo<E> manualPaging(List&lt;E> list) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() - 1;
        if (pageNum < 0) {
            pageNum = 0;
        }
        int pageSize = pageDomain.getPageSize();
        int totalSize = 0;
        int totalPage = 0;
        // 计算总页数
        totalSize = list.size();
        totalPage = cn.hutool.core.util.PageUtil.totalPage(totalSize, pageSize);
        if (pageNum <= totalPage) {
            // 分页
            list = CollUtil.page(pageNum, pageSize, list);
        } else {
            list = ListUtil.list(false);
        }
        PageInfo<E> of = PageInfo.of(list);
        /**
         * 是否为第一页
         */
        boolean isFirstPage = false;
        /**
         * 是否为最后一页
         */
        boolean isLastPage = false;
        /**
         * 是否有前一页
         */
        boolean hasPreviousPage = false;
        /**
         * 是否有下一页
         */
        boolean hasNextPage = false;
        of.setTotal(totalSize);
        of.setPages(totalPage);
        of.setIsFirstPage(false);
        of.setIsLastPage(false);
        if (pageNum == 0) {
            of.setIsFirstPage(true);
        }
        if (pageNum == totalPage - 1) {
            of.setIsLastPage(true);
        }
        if (pageNum < totalPage - 1) {
            of.setHasNextPage(true);
        }
        if (pageNum != 0) {
            of.setHasPreviousPage(true);
        }
        return of;
    }
}
}
}
