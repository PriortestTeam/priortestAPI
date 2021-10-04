package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.MailDto;

/**
 * @author xwf
 * @date 2021/9/12 22:54
 */
public interface  MailService {

    void sendTextMail(MailDto mailDO);

    void sendHtmlMail(MailDto mailDO,boolean isShowHtml);

    void sendTemplateMail(MailDto mailDO);
}
