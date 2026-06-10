package com.example.XSJ.Pojo.User;

import cn.hutool.json.JSON;
import lombok.Data;

import java.sql.Date;

@Data
public class User {
    /**
     * 用户ID（主键）
     */
    private String userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 编码
     */
    private String enCode;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别（0-未知/1-男/2-女）
     */
    private Integer gender;

    /**
     * 生日
     */
    private Date birthDay;


    /**
     * 删除标记（0-未删除/1-已删除）
     */
    private Integer deleteMark;

    /**
     * 状态（0-禁用/1-启用）
     */
    private Integer status;

    /**
     * 角色基础标识
     */
    private int roleBase;

    /**
     * 是否存在会员快速判断身份
     */
    private String uiMember;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
    注册ip
     */
    private  String registerIp;
    /**
     * 用户权重
     */
    private JSON UserTagWeight;
    /**
     * 学历水平
     */
    private  String Leavel;

    /**
     *  用户地址 Address
     */
    private  String Address;
    /**
     * 国家
     */
    private  String Country;
    /**
     * 学校
     */
    private  String School;
    /**
     * Language 语言
     */
    private  String Language;
    /**
     *  年龄
     */
    private  int Age;



    /**
     * 账户状态记录ID（主键）
     */
    private String accountStatusId;

    /**
     * 1禁言，2禁止发布视频3禁止开课4禁止购买会员5禁止购买课程6禁止添加好友7禁止登录
     */
    private String accountStatusType;

    /**
     * 状态生效时间
     */
    private java.util.Date startTime;

    /**
     * 状态失效时间
     */
    private java.util.Date endTime;

    /**
     * 状态变更原因
     */
    private String reason;



}
