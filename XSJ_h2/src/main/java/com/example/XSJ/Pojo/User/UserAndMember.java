package com.example.XSJ.Pojo.User;

import java.util.Date;

public class UserAndMember {

    /**
     * 关联记录ID（主键）
     */
    private String usersAndMemberId;

    /**
     * 用户ID（关联用户表）
     */
    private String userId;

    /**
     * 会员ID（关联会员表）
     */
    private String memberId;

    /**
     * 会员开始时间
     */
    private Date memberStartTime;

    /**
     * 会员结束时间
     */
    private Date memberEndTime;

    /**
     * 状态（0-失效/1-生效）
     */
    private Integer status;
}
