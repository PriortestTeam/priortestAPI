package com.hu.oneclick.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.util.MapUtils;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.ScopeEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.OneFilter;
import com.hu.oneclick.model.entity.View;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.util.ArrayList;
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
@RequestMapping("/bean/search");
@Tag(name = "复杂查询统一管理", description = "复杂查询统一管理相关接口");
@Slf4j


public class BeanSearchController {

    private static final Logger log = LoggerFactory.getLogger(BeanSearchController.class);
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
    @Resource
    private JwtUserServiceImpl jwtUserService;


    @Operation(summary = "通用查询");
    @GetMapping("/{scope}/{viewId}");
    public Resp<PageInfo<?>> generalQuery(@Parameter(description = "范围") @PathVariable String scope,
                                           @Parameter(description = "视图ID") @PathVariable Long viewId
    ) {
        ScopeEnum scopeEnum = ScopeEnum.getByName(scope);
        if (scopeEnum == null) {
            log.error("范围不存在");
            return new Resp.Builder<PageInfo<?>>().ok();
        }
        // 根据范围寻找具体查询类
        Class<?> scopeClass;
        try {
            scopeClass = Class.forName(scopeEnum.getBeanPath();
        } catch (ClassNotFoundException e) {
            log.error("根据范围寻找具体查询类失败");
            return new Resp.Builder<PageInfo<?>>().fail();
        }
        // 获取当前用户projectId
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        if (StrUtil.isBlank(projectId) {
            log.error("获取项目异常");
            return new Resp.Builder<PageInfo<?>>().ok();
        }

        List&lt;List&lt;OneFilter>> lst = new ArrayList&lt;>();

        // 查询视图
        View view1 = viewService.getById(viewId);

        this.processAllFilter(view1, lst);

        if (CollUtil.isEmpty(lst) {
            return new Resp.Builder<PageInfo<?>>().setData(PageUtil.manualPaging(mapSearcher.searchAll(scopeClass, MapUtils.builder().build().ok();
        }
        Map&lt;String, Object> params = this.processParam(lst, projectId);

        List&lt;Map&lt;String, Object>> list = mapSearcher.searchAll(scopeClass, params);
        // 物理分页
        return new Resp.Builder<PageInfo<?>>().setData(PageUtil.manualPaging(list).ok();
    }

    private Map&lt;String, Object> processParam(List&lt;List&lt;OneFilter>> lst, String projectId){
        Map&lt;String, Object> params = new LinkedHashMap&lt;>();
        params.put("P0.projectId", projectId);
        params.put("P0.projectId-op", "eq");

        // 参数增加逻辑关系
        StringBuilder gexpr = new StringBuilder();
        gexpr.append("P0");

        int j =0;
        for(List&lt;OneFilter> oneFilters : lst){
            gexpr.append("&(");
            for (int i = 0; i < oneFilters.size(); i++) {
                String fieldName = StrUtil.format("A_{}_{}", j, i);
                params.put(StrUtil.format("{}.{}", fieldName,oneFilters.get(i).getFieldNameEn(), oneFilters.get(i).getSourceVal();
                params.put(StrUtil.format("{}.{}-op",fieldName, oneFilters.get(i).getFieldNameEn(), oneFilters.get(i).getCondition();
                if(i==0){
                    gexpr.append(fieldName);
                }else{
                    gexpr.append(oneFilters.get(i).getAndOr().equals("and") ? "&" : "|");
                    gexpr.append(fieldName);
                }
            }
            gexpr.append(")");
            j= j+1;
        }
        params.put("gexpr", gexpr.toString();

        return params;
    }
    private void processAllFilter(View view, List&lt;List&lt;OneFilter>> lst){
        if(StringUtils.isNotEmpty(view.getParentId() && view.getLevel() > 0){
            View tempView = viewService.getById(view.getParentId();
            this.processAllFilter(tempView, lst);

            lst.add(view.getOneFilters();
        }else{
            lst.add(view.getOneFilters();
        }
    }

}