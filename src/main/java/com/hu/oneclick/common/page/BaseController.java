package com.hu.oneclick.common.page;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.util.PageUtil;

import java.util.List;


/**
 * web层通用数据处理
 *
 * @author xiaohai
 * @date 2023/03/06
 */
public class BaseController {

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageUtil.startPage();
    }

    /**
     * 转换分页数据
     */
    protected <E, T> PageInfo<T> convertPageInfo(List<E> list, Class<T> tClass) {
        return PageUtil.convertPageInfo(list, tClass);
    }

    /**
     * 清理分页的线程变量
     */
    protected void clearPage() {
        PageUtil.clearPage();
    }

}
