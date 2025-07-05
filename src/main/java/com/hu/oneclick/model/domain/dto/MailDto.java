package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author xwf
 * @date 2021/9/12 22:51
 *  邮件接收参数
 */
@Data
public class MailDto {


    //邮件标题
    private String title;

    //内容
    private  String content;

    //接收人邮件地址
    private String toEmail;

    //html模板名称，在项目templates/mailtpl/下
    private String templateHtmlName;

    //附加，value 文件的绝对地址/动态模板数据
    private Map<String, Object> attachment;



}
