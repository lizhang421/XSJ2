package com.example.XSJ.utils;

import cn.hutool.core.util.RandomUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    //加密
    public static String encode(String password) {
        String salt = RandomUtil.randomString(20);
        return encode(password, salt);
    }

    private static String encode(String password, String salt) {
        try {
            // 1. 获取SHA-256加密实例
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 2. 密码+盐值拼接，转字节数组
            byte[] mixBytes = (password + salt).getBytes(StandardCharsets.UTF_8);
            // 3. 加密
            byte[] digest = md.digest(mixBytes);
            //
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return salt + "@" + sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }

    //验证密码
    public static Boolean matches(String encodedPassword, String rawPassword) {
        if (encodedPassword == null || rawPassword == null) {
            return false;
        }
        if (!encodedPassword.contains("@")) {
            throw new RuntimeException("密码格式不正确！");
        }
        String[] arr = encodedPassword.split("@");
        String salt = arr[0];
        return encodedPassword.equals(encode(rawPassword, salt));
    }
}