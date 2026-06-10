package com.example.XSJ.utils;

import cn.hutool.core.util.RandomUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import static com.example.XSJ.utils.RegexPatterns.*;

/**
 * 邮箱验证码工具类（整合Caffeine缓存 + Spring邮箱配置）
 */
@Slf4j
@Component
public class EmailVerifyUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    // 注入 Caffeine 缓存管理器
    @Autowired
    private CacheManager cacheManager;

    /**
     * 生成6位数字验证码
     * @return 6位随机数字验证码
     */
    public String generateVerifyCode() {
        return RandomUtil.randomNumbers(CODE_LENGTH);
    }

    /**
     * 发送邮箱验证码
     * @param toEmail 收件人邮箱
     * @param type 1=登录 2=注册 3=忘记密码
     * @return true 成功 / false 失败
     */
    public String sendVerifyCode(String toEmail,int type) {
        //  校验邮箱格式
        if (toEmail == null || !toEmail.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            log.error("邮箱格式错误：{}", toEmail);
            return "邮箱格式错误";
        }
        Cache cache = null;
        if (type == 1) {
            cache = cacheManager.getCache(CACHE_NAME);
        }else if (type == 2) {
            cache = cacheManager.getCache(CACHE_NAME_REGISTER);
        }else if (type == 3) {
            // 新增：忘记密码缓存（你常量里加一个即可）
            cache = cacheManager.getCache(CACHE_NAME_FORGET_PWD);
        }else {
            return "系统异常";
        }
        String verifyCode = generateVerifyCode();

        try {
            cache.put(toEmail, verifyCode);
            log.info("邮箱{}的验证码{}已缓存，有效期五分钟", toEmail, verifyCode);
            //创建消息对象
            MimeMessage mineMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mineMessage, true, "UTF-8");

            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) javaMailSender;
            //官方账号信息
            helper.setFrom(new InternetAddress(mailSenderImpl.getUsername(), SEND_NICK, "UTF-8"));

            helper.setTo(toEmail);
            helper.setSubject(EMAIL_SUBJECT);

            // 根据类型生成不同邮件内容
            String emailContent = buildEmailContent(verifyCode, type);
            helper.setText(emailContent, true);

            javaMailSender.send(mineMessage);
            log.info("邮箱{}的验证码发送成功", toEmail);
            return "验证码成功发送，请注意查收";

        } catch (Exception e) {
            log.error("发送邮箱验证码失败，收件人：{}", toEmail, e);
            if (cache != null) {
                cache.evict(toEmail);
            }
            return "系统异常";
        }
    }

    /**
     * 验证验证码是否正确（验证后自动删除缓存，防止重复使用）
     * @param toEmail 收件人邮箱
     * @param inputCode 用户输入的验证码
     * @param type 1=登录 2=注册 3=忘记密码
     * @return 验证结果
     */
    public boolean verifyCode(String toEmail, String inputCode,int type) {
        if (toEmail == null || inputCode == null || inputCode.length() != CODE_LENGTH) {
            log.error("验证参数错误：邮箱={}，输入验证码={}", toEmail, inputCode);
            return false;
        }

        Cache cache=null;
        if (type == 1) {
            cache = cacheManager.getCache(CACHE_NAME);
        }else if (type == 2) {
            cache = cacheManager.getCache(CACHE_NAME_REGISTER);
        }else if (type == 3) {
            // 新增：忘记密码验证码校验
            cache = cacheManager.getCache(CACHE_NAME_FORGET_PWD);
        }else {
            return false;
        }

        if (cache == null) {
            log.error("验证码缓存实例初始化失败");
            return false;
        }

        Cache.ValueWrapper valueWrapper = cache.get(toEmail);
        if (valueWrapper == null) {
            log.warn("邮箱{}的验证码已过期或未发送", toEmail);
            return false;
        }

        String realCode = (String) valueWrapper.get();
        boolean isMatch = realCode.equals(inputCode);

        if (isMatch) {
            cache.evict(toEmail);
            log.info("邮箱{}的验证码验证成功，已删除缓存", toEmail);
        } else {
            log.warn("邮箱{}的验证码验证失败，输入：{}，实际：{}", toEmail, inputCode, realCode);
        }
        return isMatch;
    }

    /**
     * 构建邮件HTML内容（支持类型：登录/注册/忘记密码）
     */
    private String buildEmailContent(String verifyCode, int type) {
        String title = "";
        String tip = "";

        if (type == 1) {
            title = "登录验证";
            tip = "你正在进行登录操作";
        } else if (type == 2) {
            title = "注册验证";
            tip = "你正在进行账号注册操作";
        } else if (type == 3) {
            title = "忘记密码验证";
            tip = "你正在进行找回密码操作";
        } else {
            title = "账号安全验证";
            tip = "你正在进行敏感操作";
        }

        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>验证码邮件</title>" +
                "</head>" +
                "<body style='font-family:微软雅黑, Arial, sans-serif; line-height:1.8; color:#333;'>" +
                "    <div style='max-width:600px; margin:0 auto; padding:20px; border:1px solid #eee; border-radius:8px;'>" +
                "        <h2 style='color:#e64340; text-align:center; margin-bottom:20px;'>XSJ-AI视频教学平台</h2>" +
                "        <p>你好！</p>" +
                "        <p>" + tip + "，本次验证码为：</p>" +
                "        <p style='font-size:24px; font-weight:bold; color:#e64340; text-align:center; margin:20px 0;'>" + verifyCode + "</p>" +
                "        <p>该验证码有效期为5分钟，请及时完成验证，请勿泄露给他人！</p>" +
                "        <p style='color:#999; font-size:12px; margin-top:30px;'>如非本人操作，请忽略此邮件，我们不会向你索要验证码。</p>" +
                "        <p style='color:#999; font-size:12px; margin-top:30px;'>如有问题请联系XSJ教学开发平台技术组</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}