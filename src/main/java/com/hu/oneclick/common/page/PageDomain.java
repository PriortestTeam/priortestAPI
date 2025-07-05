package com.hu.oneclick.common.page;
import cn.hutool.core.convert.Convert;
import com.hu.oneclick.common.util.ServletUtil;
import lombok.Getter;
import lombok.Setter;
/**
 * 分页数据
 *
 * @author xiaohai
 * @date 2023/03/06
 */
@Getter
@Setter


public class PageDomain {
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";
    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";
    /**
     * 当前记录起始索引
     */
    private Integer pageNum;
    /**
     * 每页显示记录数
     */
    private Integer pageSize;
    private String orderBy;
    public Integer getPageNum() {
        return pageNum;
    }
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public String getOrderBy() {
        return orderBy;
    }
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtil.getParameter(PAGE_NUM), 1);
        pageDomain.setPageSize(Convert.toInt(ServletUtil.getParameter(PAGE_SIZE), 20);
        return pageDomain;
    }
}