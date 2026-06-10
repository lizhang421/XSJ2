package com.example.XSJ.Pojo.UserDTO;

import lombok.Data;

import java.sql.Date;

@Data
public class Userandvideinterseat {

    /**
     * 真实姓名
     */
    private String realName;
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


    private Integer isLike;
    private Integer isCollect;
    private Integer isComment;
    private Integer isShare;
    private Integer playPercent;  // 播放百分比
}
