package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

@Data
public class UserResourcesDTO {





    private String enCode;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否存在会员快速判断身份
     */
    private String uiMember;

    /**
     * 国家
     * 【修改3】字段名小写开头（驼峰规范）
     */
    private String country;

    /**
     * 学校
     * 【修改4】字段名小写开头（驼峰规范）
     */
    private String school;

    /**
     *
     * 背景
     */
    private String backImage;

}
