package com.example.XSJ.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    //从yml中读取配置信息，需要自己修改成自己的
    //读取的邮箱账号
    @Value("${email.sender.username}")
     private String emailUsername;
    //读取授权码
    @Value("${email.sender.auth-code}")
    private String emailAuthCode;

    @Value("${email.smtp.host}")
    private String smtpHost;
    @Value("${email.smtp.port}")
    private String smtpPort;
    @Value("${email.smtp.ssl}")
    private String smtpSsl;

    /**
     * 版本要在3.0之上
     * @return
     */
    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        //=======邮箱配置====//
        mailSender.setHost(smtpHost);
        mailSender.setPort(Integer.parseInt(smtpPort));
        mailSender.setUsername(emailUsername); // 读取yml中的邮箱账号
        mailSender.setPassword(emailAuthCode); // 读取yml中的授权码

        // 加密配置
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", smtpSsl);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "true");

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }


}
