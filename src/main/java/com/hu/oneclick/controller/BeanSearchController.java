package com.hu.oneclick.controller;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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


//    @GetMapping("/feature/{viewId}")
//    public Resp<String> queryDoesExistByTitle(@ApiParam("视图ID") @PathVariable Long viewId){
//        // 查询视图
//        View view = viewService.queryById(viewId);
//        return new Resp.Builder<PageInfo<TestCycle>>().setData(PageInfo.of(viewId)).ok();
//    }


}
