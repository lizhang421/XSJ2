package com.example.XSJ.utils;

import com.example.XSJ.Pojo.Result;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录注册防刷设置
 */


public class LocalAntiBrushService {
    /**
     * 设置各种防刷属性的基本信息
     */
    //单ip请求次数keyip value 请求次数 五分钟给过期
     Cache<String, AtomicInteger>ipRequestCache= Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .maximumSize(10000)
        .build();
    /**
     * 单个账号密码错误计数 key 账号value错误次数
     */
    Cache<String,AtomicInteger>accountPwdErrorCache=Caffeine.newBuilder()
            .expireAfterWrite(10,TimeUnit.MINUTES)
            .maximumSize(5000)
            .build();
    /**
     *单个设备ip验证码请求次数
     */
    Cache<String,AtomicInteger>deviceCaptchaCache=Caffeine.newBuilder()
            .expireAfterWrite(10,TimeUnit.MINUTES)
            .maximumSize(8000)
            .build();
    /**
     * 登录ip限流
     * 十分钟 10 次机会
     */
    Cache<String,AtomicInteger>loginCaptchaCache=Caffeine.newBuilder()
            .expireAfterWrite(10,TimeUnit.MINUTES)
            .maximumSize(8000)
            .build();
    /**
     * 邮箱登录限流
     * 十分钟十次机会，超过之后只能去等着,统一计数防止高并发计数混乱
     */
    Cache<String,AtomicInteger>emailLoginCodeCache= Caffeine.newBuilder()
            .expireAfterWrite(5,TimeUnit.MINUTES)
            .maximumSize(8000)
            .build();
    /**
     * 邮箱注册限流
     * 十分钟十次机会，超过之后只能去等着,统一计数防止高并发计数混乱
     */
    Cache<String,AtomicInteger>emailRegisterCodeCache= Caffeine.newBuilder()
            .expireAfterWrite(5,TimeUnit.MINUTES)
            .maximumSize(8000)
            .build();



    /**
     * -----------配置限流规则----
     */
    private static final  int IP_REQUEST_LIMIT=20;
    private static final  int ACCOUNT_PWD_ERROR_LIMIT=5;
    private static final  int DEVICE_CAPTCHA_LIMIT=15;
    private static final  int LOGIN_CAPTCHA_LIMIT=15;
    private static final  int EMAIL_CODE_LIMIT=5;  //五分钟内有五次机会
    private static final  int EMAIL_REGISTER_CODE_LIMIT=10; //邮箱登录五分钟有十次获取验证码机会



    Cache<String,AtomicInteger> emailForgetPwdCodeCache= Caffeine.newBuilder()
            .expireAfterWrite(5,TimeUnit.MINUTES)
            .maximumSize(8000)
            .build();

    /**
     * 忘记密码邮箱获取次数限制 5分钟5次
     */
    private static final int EMAIL_FORGET_PWD_CODE_LIMIT = 5;

    /**
     * 校验忘记密码邮箱验证码限流
     */
    public boolean checkEmailForgetPwdCodeLimit(String email) {
        return incrementAndCheck(email, emailForgetPwdCodeCache, EMAIL_FORGET_PWD_CODE_LIMIT);
    }

    /**
     * 清除忘记密码邮箱限流缓存
     */
    public void clearEmailForgetPwdCaptchaCount(String email) {
        emailForgetPwdCodeCache.invalidate(email);
    }




    /**
     * 注册单IP限流校验
     * @param ip 请求IP
     * @return true=触发限流，false=正常
     */
    public boolean checkIpLimit(String ip) {
        return incrementAndCheck(ip, ipRequestCache, IP_REQUEST_LIMIT);
    }

    /**
     * 单账号密码错误限流校验
     * @param account 账号
     * @return true=触发限流（冻结账号），false=正常
     */
    public boolean checkAccountPwdErrorLimit(String account) {

        return incrementAndCheck(account, accountPwdErrorCache, ACCOUNT_PWD_ERROR_LIMIT);
    }

    /**
     * 单设备验证码请求限流校验
     * @param deviceId 设备ip
     * @return true=触发限流，false=正常
     */
    public boolean checkDeviceCaptchaLimit(String deviceId) {
        return incrementAndCheck(deviceId, deviceCaptchaCache, DEVICE_CAPTCHA_LIMIT);
    }
    /**
     * 登录限流
     */
    public boolean checkLoginCaptchaLimit(String ip) {
        return incrementAndCheck(ip, loginCaptchaCache, LOGIN_CAPTCHA_LIMIT);
    }

    /**
     * 登录邮箱验证码限流
     */
    public  boolean checkEmailLoginCodeLimit(String email) {
        return incrementAndCheck(email, emailLoginCodeCache, EMAIL_CODE_LIMIT);
    }

    /**
     * 注册邮箱验证码限流
     */
    public boolean checkEmailRegisterCodeLimit(String email) {
        return incrementAndCheck(email,emailRegisterCodeCache, EMAIL_REGISTER_CODE_LIMIT);
    }



    /**
     * 限流
     */
    public boolean incrementAndCheck(String key,Cache<String,AtomicInteger> cache,int limit){
        //获取初始计数 AtomicInteger实现线程安全
        AtomicInteger count =cache.get(key,k-> new AtomicInteger(0));
        //计数自增
        int currentCount=count.incrementAndGet();
        return currentCount>limit;
    }

    public void  clearIpCount(String ip) {
        ipRequestCache.invalidate(ip);
    }

    public void clearAccountPwdErrorCount(String account) {
        accountPwdErrorCache.invalidate(account);
    }

    public void clearDeviceCaptchaCount(String deviceId) {
        deviceCaptchaCache.invalidate(deviceId);
    }

    /**
     * 清楚邮箱登录验证码缓存
     */
     public void clearEmailLoginCaptchaCount(String email) {
         emailLoginCodeCache.invalidate(email);
     }
    /**
     * 清除邮箱注册验证码缓存
     */
    public void clearEmailRegisterCaptchaCount(String email) {
        emailRegisterCodeCache.invalidate(email);
    }









}
