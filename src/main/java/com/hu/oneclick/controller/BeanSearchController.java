package com.hu.oneclick.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.util.MapUtils;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.ScopeEnum;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.OneFilter;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 复杂查询统一管理
 *
 * @author xiaohai
 * @date 2023/08/20
 */
@RestController
@RequestMapping("/bean/search")
@Api(tags = "复杂查询统一管理")
@Slf4j
public class BeanSearchController {

    /**
     * 注入 Map 检索器，它检索出来的数据以 Map 对象呈现
     */
    @Resource
    private MapSearcher mapSearcher;
    /**
     * 注入 Bean 检索器，它检索出来的数据以 泛型 对象呈现
     */
    @Resource
    private BeanSearcher beanSearcher;
    @Resource
    private ViewService viewService;


    @ApiOperation("通用查询")
    @GetMapping("/{scope}/{viewId}")
    public Resp<PageInfo<?>> generalQuery(@ApiParam("范围") @PathVariable String scope,
                                           @ApiParam("视图ID") @PathVariable Long viewId,
                                           HttpServletRequest request
    ) {
        ScopeEnum scopeEnum = ScopeEnum.getByName(scope);
        if (scopeEnum == null) {
            log.error("范围不存在");
            return new Resp.Builder<PageInfo<?>>().ok();
        }
        // 根据范围寻找具体查询类
        Class<?> scopeClass;
        try {
            scopeClass = Class.forName(scopeEnum.getBeanPath());
        } catch (ClassNotFoundException e) {
            log.error("根据范围寻找具体查询类失败");
            return new Resp.Builder<PageInfo<?>>().fail();
        }

        // 查询视图
        View view = viewService.getById(viewId);
        // 获取过滤条件
        List<OneFilter> oneFilters = view.getOneFilters();
        if (CollUtil.isEmpty(oneFilters)) {
            return new Resp.Builder<PageInfo<?>>().setData(PageUtil.manualPaging(mapSearcher.searchAll(scopeClass, MapUtils.builder().build()))).ok();
        }
        // 构建过滤参数参数
        Map<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < oneFilters.size(); i++) {
            params.put(StrUtil.format("A{}.{}", i, oneFilters.get(i).getFieldNameEn()), oneFilters.get(i).getSourceVal());
            params.put(StrUtil.format("A{}.{}-op", i, oneFilters.get(i).getFieldNameEn()), oneFilters.get(i).getCondition());
        }
        // 参数增加逻辑关系
        StringBuilder gexpr = new StringBuilder();
        gexpr.append("A0");
        for (int i = 1; i < oneFilters.size(); i++) {
            gexpr.append(oneFilters.get(i).getAndOr().equals("and") ? "&" : "|");
            gexpr.append(StrUtil.format("A{}", i));
        }
        params.put("gexpr", gexpr.toString());

        List<Map<String, Object>> list = mapSearcher.searchAll(scopeClass, params);
        // 物理分页
        return new Resp.Builder<PageInfo<?>>().setData(PageUtil.manualPaging(list)).ok();
    }

}
