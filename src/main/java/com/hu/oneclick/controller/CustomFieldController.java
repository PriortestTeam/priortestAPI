package com.hu.oneclick.controller;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.CustomFieldService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * @author qingyang
 */
@RestController
@RequestMapping("customField")
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    public CustomFieldController(CustomFieldService customFieldService) {
        this.customFieldService = customFieldService;
    }

    //custom begin

    @PostMapping("queryCustomList")
    public Resp<List<CustomField>> queryCustomList(@RequestBody CustomField customField){
        return customFieldService.queryCustomList(customField);
    }

    //custom end

    //radio begin

    @GetMapping("queryFieldRadioById/{customFieldId}")
    public Resp<FieldRadio> queryFieldRadioById(@PathVariable String customFieldId){
        return customFieldService.queryFieldRadioById(customFieldId);
    }

    @PostMapping("addCustomRadio")
    public Resp<String> addCustomRadio(@RequestBody FieldRadio fieldRadio){
        return customFieldService.addCustomRadio(fieldRadio);
    }

    @PostMapping("updateCustomRadio")
    public Resp<String> updateCustomRadio(@RequestBody FieldRadio fieldRadio){
        return customFieldService.updateCustomRadio(fieldRadio);
    }

    @DeleteMapping("deleteCustomRadio/{customFieldId}")
    public Resp<String> deleteCustomRadio(@PathVariable String customFieldId){
        return customFieldService.deleteCustomRadio(customFieldId);
    }


    //radio end

    //text begin


    @GetMapping("queryFieldTextById/{customFieldId}")
    public Resp<FieldText> queryFieldTextById(@PathVariable String customFieldId){
        return customFieldService.queryFieldTextById(customFieldId);
    }

    @PostMapping("addCustomText")
    public Resp<String> addCustomText(@RequestBody FieldText fieldText){
        return customFieldService.addCustomText(fieldText);
    }

    @PostMapping("updateCustomText")
    public Resp<String> updateCustomText(@RequestBody FieldText fieldText){
        return customFieldService.updateCustomText(fieldText);
    }

    @DeleteMapping("deleteCustomText/{customFieldId}")
    public Resp<String> deleteCustomText(@PathVariable String customFieldId){
        return customFieldService.deleteCustomText(customFieldId);
    }

    //text end

    //rich text begin

    @PostMapping("addCustomRichText")
    public Resp<String> addCustomRichText(@RequestBody FieldRichText fieldRichText){
        return customFieldService.addCustomRichText(fieldRichText);
    }

    @PostMapping("updateCustomRichText")
    public Resp<String> updateCustomRichText(@RequestBody FieldRichText fieldRichText){
        return customFieldService.updateCustomRichText(fieldRichText);
    }

    //rich text end

    //drop down begin

    @GetMapping("queryFieldDropDownById/{customFieldId}")
    public Resp<FieldDropDown> queryFieldDropDownById(@PathVariable String customFieldId){
        return customFieldService.queryFieldDropDownById(customFieldId);
    }

    @PostMapping("addCustomDropDown")
    public Resp<String> addCustomDropDown(@RequestBody FieldDropDown fieldDropDown){
        return customFieldService.addCustomDropDown(fieldDropDown);
    }

    @PostMapping("updateCustomDropDown")
    public Resp<String> updateCustomDropDown(@RequestBody FieldDropDown fieldDropDown){
        return customFieldService.updateCustomDropDown(fieldDropDown);
    }

    @DeleteMapping("deleteCustomDropDown/{customFieldId}")
    public Resp<String> deleteCustomDropDown(@PathVariable String customFieldId){
        return customFieldService.deleteCustomDropDown(customFieldId);
    }

    //drop down end
}
