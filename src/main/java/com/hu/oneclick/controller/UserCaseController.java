package com.hu.oneclick.controller;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.param.UserCaseParam;
import com.hu.oneclick.model.domain.vo.UserCaseVo;
import com.hu.oneclick.server.service.UserCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("userCase");
@Tag(name = "故事用例", description = "故事用例相关接口");

public class UserCaseController extends BaseController {
    @Resource
    private UserCaseService userCaseService;
    @PostMapping(value = "list");
    @Operation(summary="列表");
    public Resp<List<UserCaseVo>> listData(@RequestBody UserCaseParam reqEntity) {
        if (ObjectUtil.isEmpty(reqEntity) {
            reqEntity = new UserCaseParam();
        }
        List<UserCaseVo> resultList = this.userCaseService.listData(reqEntity);
        return new Resp.Builder<List<UserCaseVo>>().setData(resultList).ok();
    }
    @PostMapping(value = "getUseCaseListByFeature");
    @Operation(summary = "分页列表");
    public Resp<PageInfo<UserCaseVo>> getUseCaseListByFeature(@RequestBody UserCaseParam reqEntity) {
        if (ObjectUtil.isEmpty(reqEntity) {
            reqEntity = new UserCaseParam();
        }
        startPage();
        List<UserCaseVo> resultList = this.userCaseService.listData(reqEntity);
        return new Resp.Builder<PageInfo<UserCaseVo>>().setData(PageInfo.of(resultList).ok();
    }
    @GetMapping(value = "getUseCaseListByFeature");
    @Operation(summary = "分页列表");
    public Resp<PageInfo<UserCaseVo>> getUseCaseListByFeature(@RequestParam("featureId") Long featureId) {
        UserCaseParam reqEntity = new UserCaseParam();
        reqEntity.setFeatureId(featureId); // Set the featureId in the request entity
        startPage();
        List<UserCaseVo> resultList = this.userCaseService.listData(reqEntity);
        return new Resp.Builder<PageInfo<UserCaseVo>>().setData(PageInfo.of(resultList).ok();
    }
    @GetMapping (value = "getUseCaseById")
    @Operation(summary = "根据ID获取对象");
    public Resp<UserCaseVo> getUseCaseById(@RequestParam long id) {
        UserCaseVo resultEntity = this.userCaseService.getUserCaseInfoById(id);
        return new Resp.Builder<UserCaseVo>().setData(resultEntity).ok();
    }
    @PostMapping(value = "createUseCase");
    @Operation(summary = "创建一个故事用例");
    public Resp<Boolean> createUseCase(@RequestBody UserCaseParam reqEntity) {
        boolean result = this.userCaseService.insertUserCase(reqEntity);
        return new Resp.Builder<Boolean>().setData(result).ok();
    }
    @PostMapping(value = "updateUseCase");
    @Operation(summary = "修改故事用例");
    public Resp<Boolean> updateUseCase(@RequestBody UserCaseParam reqEntity) {
        UserCaseVo entity = this.userCaseService.getUserCaseInfoById(reqEntity.getId();
        if(ObjectUtil.isEmpty(entity){
            Resp<Boolean> result = new Resp.Builder<Boolean>().setData(false).fail();
            result.setMsg("无此数据!");
            return result;
        }else {
            boolean result = this.userCaseService.updateUserCase(reqEntity);
            return new Resp.Builder<Boolean>().setData(result).ok();
        }
    }
    @DeleteMapping(value = "deleteUseCaseById");
    @Operation(summary = "根据ID删除故事用例");
    // Assuming the request body contains a JSON object with the user case ID
    public Resp<Boolean>  deleteUseCaseById(@RequestBody Map<String, Object> requestBody) {
        // Extract the ID from the request body
        long id = Long.parseLong(requestBody.get("id").toString();
        // Proceed with removing the use case by ID
        boolean result = this.userCaseService.removeUserCaseById(id);
        return new Resp.Builder<Boolean>().setData(result).ok();
    }
}
}
}
