package com.example.XSJ.Pojo.UserDTO;

import cn.hutool.json.JSON;
import lombok.Data;

import java.sql.Date;

@Data
public class UserMessageDTO {

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
     * 角色基础标识
     */
    private String roleBase;

    /**
     * 是否存在会员快速判断身份
     */
    private String uiMember;

    /**
     * 创建时间
     */
    private Date createTime;
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
     *  账号状态
     */
    private  String Status;



}
