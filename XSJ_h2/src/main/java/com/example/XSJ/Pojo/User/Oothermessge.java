package com.example.XSJ.Pojo.User;

import lombok.Data;

import java.sql.Date;

@Data
public class Oothermessge {


    /**
     * 昵称
     */
    private String nickName;


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
     * 学历水平
     */
    private  String Leavel;


    /**
     * 国家
     */
    private  String Country;
    /**
     * 学校
     */
    private  String School;

    /**
     *  年龄
     */
    private  int Age;

    private  String backImage;


}
