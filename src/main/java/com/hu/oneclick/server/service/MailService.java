package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.MailDto;

import javax.mail.MessagingException;

/**
 * @author xwf
 * @date 2021/9/12 22:54
 */
public interface  MailService {

    void sendTextMail(MailDto mailDO);

    void sendHtmlMail(MailDto mailDO,boolean isShowHtml);

    void sendTemplateMail(MailDto mailDO);

    void sendSimpleMail(String to, String subject, String contnet);

    void sendAttachmentsMail(String to, String subject, String contnet,
                             String filePath, String sendName) throws MessagingException;
}
