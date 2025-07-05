package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.model.domain.dto.MailDto;
import com.hu.oneclick.server.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author xwf
 * @date 2021/9/12 22:56
 * 发送邮件
 */
@Service
public class MailServiceImpl  implements MailService {

    private final static Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    //template模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}");
    private String from;

    @Override
    public void sendTextMail(MailDto mail) {
        //建立邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // 发送人的邮箱
        message.setSubject(mail.getTitle(); //标题
        message.setTo(mail.getToEmail(); //发给谁  对方邮箱
        message.setText(mail.getContent(); //内容
        try {
            javaMailSender.send(message); //发送
        } catch (MailException e) {
            logger.error("class: MailServiceImpl#sendTextMail,error []" + e.getMessage();
        }
    }

    @Async
    @Override
    public void sendHtmlMail(MailDto mail, boolean isShowHtml) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            //是否发送的邮件是富文本（附件，图片，html等）
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(from);// 发送人的邮箱
            messageHelper.setTo(mail.getToEmail();//发给谁  对方邮箱
            messageHelper.setSubject(mail.getTitle();//标题
            messageHelper.setText(mail.getContent(),isShowHtml);//false，显示原始html代码，无效果
            //判断是否有附加图片等
            if(mail.getAttachment() != null && mail.getAttachment().size() > 0){
                mail.getAttachment().entrySet().stream().forEach(entrySet -> {
                    try {
                        File file = new File(String.valueOf(entrySet.getValue();
                        if(file.exists(){
                            messageHelper.addAttachment(entrySet.getKey(), new FileSystemResource(file);
                        }
                    } catch (MessagingException e) {
                        logger.error("class: MailServiceImpl#sendHtmlMail,error []" + e.getMessage();
                    }
                });
            }
            //发送
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("class: MailServiceImpl#sendHtmlMail,error []" + e.getMessage();
        }
    }

    @Async
    @Override
    public void sendTemplateMail(MailDto mailDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(from);// 发送人的邮箱
            messageHelper.setTo(mailDto.getToEmail();//发给谁  对方邮箱
            messageHelper.setSubject(mailDto.getTitle(); //标题
            //使用模板thymeleaf
            //Context是导这个包import org.thymeleaf.context.Context;
            Context context = new Context();
            //定义模板数据
            context.setVariables(mailDto.getAttachment();
            //获取thymeleaf的html模板
            String emailContent = templateEngine.process("/mailtpl/"+mailDto.getTemplateHtmlName(),context); //指定模板路径
            messageHelper.setText(emailContent,true);
            //发送邮件
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("class: MailServiceImpl#sendTemplateMail,error []" + e.getMessage();
        }
    }
    /**
     * 简单文本邮件
     * @param to 接收者邮件
     * @param subject 邮件主题
     * @param contnet 邮件内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String contnet){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(contnet);
        message.setFrom(from);

        javaMailSender.send(message);
    }

    /**
     * 附件邮件
     * @param to 接收者邮件
     * @param subject 邮件主题
     * @param contnet HTML内容
     * @param filePath 附件路径
     * @throws MessagingException
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String contnet,
                                    String filePath,String sendName) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(contnet, true);
        helper.setFrom(from);

        FileSystemResource file = new FileSystemResource(new File(filePath);
        helper.addAttachment(sendName, file);

        javaMailSender.send(message);
    }

}