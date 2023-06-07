package com.hu.oneclick.relation.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.relation.domain.Relation;
import com.hu.oneclick.relation.domain.param.RelationParam;
import com.hu.oneclick.relation.service.RelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 通用关系关联
 *
 * @author xiaohai
 * @date 2023/06/05
 */
@RestController
@RequestMapping("/relation")
@Slf4j
@Api(tags = "通用关系关联")
public class RelationController extends BaseController {

    @Resource
    private RelationService relationService;


    @ApiOperation("查询(根据对象)")
    @PostMapping("/object/list")
    public Resp<PageInfo<Relation>> objectList(@RequestBody RelationParam param) {
        startPage();
        List<Relation> list = relationService.getRelationListByObjectIdAndCategory(param.getObjectId(), param.getCategory());
        return new Resp.Builder<PageInfo<Relation>>().setData(PageInfo.of(list)).ok();
    }

    @ApiOperation("查询(根据目标)")
    @PostMapping("/target/list")
    public Resp<PageInfo<Relation>> targetList(@RequestBody RelationParam param) {
        startPage();
        List<Relation> list = relationService.getRelationListByTargetIdAndCategory(param.getTargetId(), param.getCategory());
        return new Resp.Builder<PageInfo<Relation>>().setData(PageInfo.of(list)).ok();
    }

    @ApiOperation("追加关系")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated Relation dto) {
        try {
            relationService.saveRelationWithAppend(dto.getObjectId(), dto.getTargetId(), dto.getCategory());
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("追加关系失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            relationService.removeByIds(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("删除关系失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCaseStep>().fail();
        }
    }
}
