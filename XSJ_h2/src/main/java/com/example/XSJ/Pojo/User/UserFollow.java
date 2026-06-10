package com.example.XSJ.Pojo.User;

import lombok.Data;

import java.util.Date;

@Data
public class UserFollow {

    /**
     * 用户关注表主键
     */
    private String userFollowId;

    /**
     * 关注者ID（关联用户表）
     */
    private String userId;

    /**
     * 被关注者ID（关联用户表）
     */
    private String followedUserId;

    /**
     * 关注状态（0-取消关注 1-关注中）
     */
    private Integer status;

    /**
     * 关注开始时间
     */
    private Date startTime;

    /**
     * 取消关注时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date creatTime;


}