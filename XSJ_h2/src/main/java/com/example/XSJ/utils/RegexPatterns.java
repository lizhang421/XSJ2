package com.example.XSJ.utils;

import com.alibaba.nacos.shaded.io.grpc.ClientStreamTracer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 这个类是用来放置正则表达式，密码手机号邮箱等等包括以后新增的验证规则都要放在这里
 */
public abstract class RegexPatterns {
    //手机号的正则表达式
    public static final String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    //邮箱的表达式
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9\\p{Punct}]{8,32}$";
    /**
     * 验证码难度
     */
    public static final int DEFAULT_POINTS = 300;

    /**
     * 设置临时文件的路径就在当先目录下面方便项目部署时候文件配置
     */
    // 先获取项目根目录，再拼接自定义上传目录，不依赖固定盘符/用户目录
    public static final String PROJECT_ROOT = System.getProperty("user.dir"); // 获取项目根目录
    public static final String FILE_ADDRESS = PROJECT_ROOT + "\\upload\\file\\";

    /**
     * ffmepg的本地路径，项目部署必须要本地部署ffmepg
     */
    public static  final  String FFMPEG_ADDRESS=PROJECT_ROOT + "\\ffmpeg-8.0.1-essentials_build\\ffmpeg-8.0.1-essentials_build\\bin\\ffmpeg.exe";

    /**
     * 解析后临时文件存放位置，这里是存放解析之后的不同分辨率的视频 在这个地方排队上传阿里云
     */
    public static final String END_ADDRESS = PROJECT_ROOT + "\\EndFile\\";

    /**
     * 验证码邮箱发送人昵称
     */
    public static final  String SEND_NICK="XSJ教学平台";
    /**
     *  这个是登录
     */
    public static final String CACHE_NAME = "login";
    /**
     * 注册
     */
    public static final String CACHE_NAME_REGISTER = "register";

    public static final String CACHE_NAME_FORGET_PWD = "forget:pwd:email:code";

    /**
     * 邮箱发送验证码长度
     */
     public   static  final  int CODE_LENGTH = 6;
    /**
     * 邮件标题
     */
     public static final String EMAIL_SUBJECT = "SXJ教学平台验证码";

    /**
     * 用户修改个人信息白名单
     */
    public static  final Set<String> ALLOWED_FIELDS = new HashSet<>(Arrays.asList(
            "RealName", "NickName","Avatar","Gender","Birthday","Leavel","Address","Country","School","Language","Age"
    ));



}
