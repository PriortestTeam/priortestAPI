package com.hu.oneclick.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhxu.bs.BeanSearcher;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.OneFilter;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
public class BeanSearchController {

    /**
     * 注入 Bean 检索器，它检索出来的数据以 泛型 对象呈现
     */
    @Resource
    private BeanSearcher beanSearcher;
    @Resource
    private ViewService viewService;
    @Resource
    private TestCaseService testCaseService;


    @ApiOperation("通用查询")
    @GetMapping("/{scope}/{viewId}")
    public Resp<PageInfo<?>> queryTestCase(@ApiParam("范围") @PathVariable String scope,
                                                  @ApiParam("视图ID") @PathVariable Long viewId,
                                           HttpServletRequest request
    ) {
        // 查询视图
        View view = viewService.getById(viewId);
        // 获取过滤条件
        List<OneFilter> oneFilters = view.getOneFilters();
        if (CollUtil.isEmpty(oneFilters)) {
            return new Resp.Builder<PageInfo<?>>().setData(PageInfo.of(testCaseService.list())).ok();
        }
        // 构建过滤参数参数
        Map<String, Object> dataMap = new LinkedHashMap<>();
        for (int i = 0; i < oneFilters.size(); i++) {
            dataMap.put(StrUtil.format("A{}.{}", i, oneFilters.get(i).getFieldNameEn()), oneFilters.get(i).getSourceVal());
            dataMap.put(StrUtil.format("A{}.{}-op", i, oneFilters.get(i).getFieldNameEn()), oneFilters.get(i).getCondition());
        }
        // 参数增加逻辑关系
        StringBuilder gexpr = new StringBuilder();
        gexpr.append("A0");
        for (int i = 1; i < oneFilters.size(); i++) {
            gexpr.append(oneFilters.get(i).getAndOr().equals("and") ? "&" : "|");
            gexpr.append(StrUtil.format("A{}", i));
        }
        dataMap.put("gexpr", gexpr);
        List<TestCase> list = beanSearcher.searchAll(TestCase.class, dataMap);
//        Map<String, Object> params = MapUtils.builder()
//                .group("1")             // A 组开始
//                .field(TestCase::getTitle, "测试")
//                .op("ct")
//                .group("2")             // B 组开始
//                .field(TestCase::getDescription, "测试")
//                .op("ct")
//                .groupExpr("1|2")   // 组间逻辑关系（组表达式）
//                .build();
//        Map<String, Object> flat = MapUtils.(request.getParameterMap());
//
//
//        List<TestCase> list = beanSearcher.searchAll(TestCase.class, params);
        return new Resp.Builder<PageInfo<?>>().setData(PageInfo.of(list)).ok();
    }


}
