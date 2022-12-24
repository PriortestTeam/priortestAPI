package com.hu.oneclick.controller;


import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.CustomFields;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.vo.CustomFieldVo;
import com.hu.oneclick.server.service.CustomFieldsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@RequestMapping("/customFields")
public class CustomFieldsController {

    @NonNull
    private final CustomFieldsService customFieldsService;

    @PostMapping("/queryCustomList")
    public Resp<List<CustomFields>> queryCustomList(@RequestBody CustomFieldDto customFieldDto) {
        return customFieldsService.queryCustomList(customFieldDto);
    }

    @PostMapping("/add")
    public Resp<String> add(@Valid @RequestBody CustomFieldVo customFieldVo) {
        return customFieldsService.add(customFieldVo);
    }

    @PutMapping ("/update")
    public Resp<String> update(@Valid @RequestBody CustomFieldVo customFieldVo) {
        if (customFieldVo.getCustomFieldId() == null) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldId" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        return customFieldsService.update(customFieldVo);
    }

    @DeleteMapping ("/delete")
    public Resp<String> delete(@Valid @RequestBody Set<Long> customFieldIds) {
        if (ObjectUtils.isEmpty(customFieldIds)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldIds" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        return customFieldsService.delete(customFieldIds);
    }
}
