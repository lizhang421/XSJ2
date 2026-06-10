package com.example.XSJ.Pojo.UserDTO;

import lombok.Data;

import java.util.Date;


@Data
public class UserFollowDTO {


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
     * 学校
     */
    private  String School;

    /**
     * 关注开始时间
     */
    private Date startTime;

    /**
     * 视频数量
     */
    private  int videoMount;
    /**
     * 粉丝数量
     */
    private  int fansMount;



}
