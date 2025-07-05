package com.hu.oneclick.controller;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.CustomFieldsDto;
import com.hu.oneclick.model.domain.vo.CustomFieldVo;
import com.hu.oneclick.model.domain.vo.CustomFileldLinkVo;
import com.hu.oneclick.server.service.CustomFieldsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
/**
 * <p>
 * 自定义字段表 前端控制器
 * </p>
 *
 * @author vince
 * @since 2022-12-13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/customFields");
@Slf4j

public class CustomFieldsController {
    @NonNull
    private final CustomFieldsService customFieldsService;
    @Page
    @GetMapping("/queryCustomList")
    public Resp<List&lt;CustomFieldVo>> queryCustomList(CustomFieldDto customFieldDto) {
        if (customFieldDto.getProjectId() == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "projectId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        log.info("queryCustomList==>customFieldDto:{}", JSON.toJSONString(customFieldDto));
        return customFieldsService.queryCustomList(customFieldDto);
    }
    @PostMapping("/add")
    public Resp<String> add(@Valid @RequestBody CustomFieldVo customFieldVo) {
        return customFieldsService.add(customFieldVo);
    }
    @PutMapping("/update")
    public Resp<String> update(@Valid @RequestBody CustomFieldVo customFieldVo) {
        if (customFieldVo.getCustomFieldId() == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        return customFieldsService.update(customFieldVo);
    }
    @DeleteMapping("/delete")
    public Resp<String> delete(@Valid @RequestBody Set<Long> customFieldIds) {
        if (ObjectUtils.isEmpty(customFieldIds)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldIds" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        return customFieldsService.delete(customFieldIds);
    }
    @GetMapping("/getAllCustomList")
    public Resp<List&lt;CustomFileldLinkVo>> getAllCustomList(CustomFieldDto customFieldDto) {
        if (customFieldDto.getProjectId() == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "projectId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        if (customFieldDto.getScopeId() == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "scopeId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        log.info("getAllCustomList==>customFieldDto:{}", JSON.toJSONString(customFieldDto));
        return customFieldsService.getAllCustomList(customFieldDto);
    }
    @GetMapping("/getAllCustomListByScopeId")
    public Resp<List&lt;CustomFileldLinkVo>> getAllCustomListByScopeId() {
        List&lt;CustomFileldLinkVo> dataList = customFieldsService.getAllCustomListByScopeId(1000001L);
        return new Resp.Builder<List&lt;CustomFileldLinkVo>>().setData(dataList).ok();
    }
    @GetMapping("/getDropDownBox")
    public Resp<List&lt;CustomFileldLinkVo>> getDropDownBox(CustomFieldDto customFieldDto) {
        if (customFieldDto.getProjectId() == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "projectId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        log.info("getDropDownBox==>customFieldDto:{}", JSON.toJSONString(customFieldDto));
        return customFieldsService.getDropDownBox(customFieldDto);
    }
    @PostMapping("/updateValueDropDownBox")
    public Resp<String> updateValueDropDownBox(@RequestBody CustomFieldsDto customFieldsDto) {
        if (null == customFieldsDto.getCustomFieldId()) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        if (StrUtil.isBlank(customFieldsDto.getFieldType())) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "fieldType" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        log.info("updateValueDropDownBox==>customFieldsDto:{}", JSON.toJSONString(customFieldsDto));
        return customFieldsService.updateValueDropDownBox(customFieldsDto);
    }
}
`}
