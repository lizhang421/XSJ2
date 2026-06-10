package com.example.XSJ.utils;

import cn.hutool.core.util.StrUtil;

/**
 *
 *
 */
public class RegexUtils {

    /**
     * 是否是无效手机格式
     * @param phone 要校验的手机号
     * @return true:符合，false：不符合
     * mismatch 方法应该是用来检查传入的字符串是否与指定的正则表达式不匹配。要
     * 是不匹配就返回 true，这意味着手机号格式无效；若匹配则返回 false，表示手机号格式有效
     */
    public static boolean isPhoneInvalid(String phone){
        return mismatch(phone, RegexPatterns.PHONE_REGEX);
    }
    /**
     * 是否是无效邮箱格式
     * @param email 要校验的邮箱
     * @return true:不符合，false：符合
     */
    public static boolean isEmailInvalid(String email){
        return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }



    /**
     * ** 是否是无效密码格式
     * true:不符合，false：符合
     * @param password
     * @return
     */
    public static boolean isPasswordInvalid(String password){
        return mismatch(password, RegexPatterns.PASSWORD_REGEX);
    }

    // 校验是否不符合正则格式
    // 这里返回true代表不合格，返回false代表合格
    private static boolean mismatch(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }

}
