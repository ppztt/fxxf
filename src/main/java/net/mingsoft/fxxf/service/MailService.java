package net.mingsoft.fxxf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @Author: yrg
 * @Date: 2020-09-19 17:02
 * @Description: TODO
 **/
@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * @param to      收件人
     * @param subject 标题
     * @param content 正文
     */
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("ts@gdcc315.cn");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

}
