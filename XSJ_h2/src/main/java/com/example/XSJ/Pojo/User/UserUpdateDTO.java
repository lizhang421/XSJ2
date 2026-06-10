package com.example.XSJ.Pojo.User;

import jakarta.validation.constraints.Email;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 用户修改个人信息 DTO
 * 仅允许修改非敏感字段，排除密码、角色、状态等核心字段
 */
@Data
public class UserUpdateDTO {

    // 真实姓名
    private String realName;

    // 昵称
    private String nickName;

    // 手机号（支持补全/修改，带格式校验）

    private String phone;

    // 邮箱（带格式校验）
    @Email(message = "邮箱格式不正确")
    private String email;

    // 头像 URL（前端直传 OSS 后返回）
    private String avatar;

    // 性别（0=未知，1=男，2=女）
    private Integer gender;

    // 生日
    private String birthday;

    // 所在学校
    private String school;

    // 学历水平
    private String leavel;

    // 地址
    private String address;

    // 国家
    private String country;

    // 使用语言
    private String language;

    // 年龄
    private Integer age;


    // 背景图片 URL
    private String backImage;
}