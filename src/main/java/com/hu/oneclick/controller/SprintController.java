package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Sprint;
import com.hu.oneclick.model.domain.dto.SprintSaveDto;
import com.hu.oneclick.model.param.SprintParam;
import com.hu.oneclick.server.service.SprintService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("sprint")
@Slf4j
public class SprintController extends BaseController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }


    @ApiOperation("列表")
    @PostMapping("/list")
    public Resp<PageInfo<Sprint>> list(@RequestBody SprintParam param) {
        if (null == param) {
            param = new SprintParam();
        }
        startPage();
        List<Sprint> dataList = this.sprintService.list(param);
        return new Resp.Builder<PageInfo<Sprint>>().setData(PageInfo.of(dataList)).ok();
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated SprintSaveDto dto) {
        try {
            Sprint sprint = this.sprintService.add(dto);
            return new Resp.Builder<Sprint>().setData(sprint).ok();
        } catch (Exception e) {
            log.error("新增迭代失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Sprint>().fail();
        }
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public Resp<Sprint> update(@RequestBody @Validated SprintSaveDto dto) {
        try {
            if (null == dto.getId()) {
                throw new BaseException("id不能为空");
            }
            Sprint feature = this.sprintService.edit(dto);
            return new Resp.Builder<Sprint>().setData(feature).ok();
        } catch (Exception e) {
            log.error("修改迭代失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Sprint>().fail();
        }
    }

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public Resp<Sprint> info(@PathVariable Long id) {
        Sprint sprint = this.sprintService.info(id);
        return new Resp.Builder<Sprint>().setData(sprint).ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            this.sprintService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除故事用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Sprint>().fail();
        }
        return new Resp.Builder<Sprint>().ok();
    }


    @ApiOperation("克隆")
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            this.sprintService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆故事用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }


}
