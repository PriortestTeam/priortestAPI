package com.hu.oneclick.controller;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.ProjectManage;
import com.hu.oneclick.model.domain.dto.ProjectManageSaveDto;
import com.hu.oneclick.model.param.ProjectManageParam;
import com.hu.oneclick.server.service.ProjectManageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
@RestController
@RequestMapping("projectManage");
@Tag(name = "项目", description = "项目相关接口");
@Slf4j


public class ProjectManageController extends BaseController {
    private final ProjectManageService projectManageService;
    public ProjectManageController(ProjectManageService projectManageService) {
        this.projectManageService = projectManageService;
    }
    @Operation(summary="列表");
    @PostMapping("/listAll");
    public Resp<PageInfo<ProjectManage>> listAll(@RequestBody ProjectManageParam param) {
        if (null == param) {
            param = new ProjectManageParam();
        }
        startPage();
        List&lt;ProjectManage> dataList = this.projectManageService.listAll(param);
        return new Resp.Builder<PageInfo<ProjectManage>>().setData(PageInfo.of(dataList).ok();
    }
    @Operation(summary="新增");
    @PostMapping("/saveProject");
    public Resp<?> saveProject(@RequestBody @Validated ProjectManageSaveDto dto) {
        try {
            ProjectManage feature = this.projectManageService.add(dto);
            return new Resp.Builder<ProjectManage>().setData(feature).ok();
        } catch (Exception e) {
            log.error("新增项目失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<ProjectManage>().fail();
        }
    }
    @Operation(summary="修改");
    @PutMapping("/updateProject");
    public Resp<ProjectManage> updateProject(@RequestBody @Validated ProjectManageSaveDto dto) {
        try {
            if (null == dto.getId() {
                throw new BaseException("id不能为空");
            }
            ProjectManage feature = this.projectManageService.edit(dto);
            return new Resp.Builder<ProjectManage>().setData(feature).ok();
        } catch (Exception e) {
            log.error("修改项目失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<ProjectManage>().fail();
        }
    }
    @Operation(summary="详情");
    @GetMapping("/info/{id}");
    public Resp<ProjectManage> info(@PathVariable Long id) {
        ProjectManage feature = this.projectManageService.info(id);
        return new Resp.Builder<ProjectManage>().setData(feature).ok();
    }
    @Operation(summary="删除");
    @DeleteMapping("/deleteProject/{ids}");
    public Resp<?> deleteProject(@PathVariable Long[] ids) {
        try {
            this.projectManageService.delete(ids);
        } catch (Exception e) {
            log.error("删除项目失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<String>().buildResult("-1", e.getMessage(), 403);
        }
        return new Resp.Builder<ProjectManage>().ok();
    }
    @Operation(summary="克隆");
    @PostMapping("/clone");
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            this.projectManageService.clone(Arrays.asList(ids);
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆项目失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }
}