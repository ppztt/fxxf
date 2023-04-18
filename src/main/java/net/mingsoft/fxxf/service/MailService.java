package net.mingsoft.fxxf.service;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * @Author: yrg
 * @Date: 2020-09-19 17:02
 * @Description: TODO
 **/
@Service
public class MailService {

//    @Autowired
//    private JavaMailSender javaMailSender;

    /**
     * @param to      收件人
     * @param subject 标题
     * @param content 正文
     */
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setFrom("ts@gdcc315.cn");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(content, true);
//        javaMailSender.send(message);
    }

}
