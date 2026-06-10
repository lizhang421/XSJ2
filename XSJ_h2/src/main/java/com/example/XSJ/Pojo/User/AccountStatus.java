package com.example.XSJ.Pojo.User;

import lombok.Data;

import java.util.Date;

@Data
public class AccountStatus {

    /**
     * 账户状态记录ID（主键）
     */
    private String accountStatusId;

    /**
     * 1禁言，2禁止发布视频3禁止开课4禁止购买会员5禁止购买课程6禁止添加好友7禁止登录
     */
    private int accountStatusType;

    /**
     * 状态生效时间
     */
    private Date startTime;

    /**
     * 状态失效时间
     */
    private Date endTime;

    /**
     * 状态变更原因
     */
    private String reason;

    /**
     * 记录创建时间
     */
    private Date createTime;
    /**
     * 是否已读
     */
    private int isRead;
    /**
     * 账户ID
     */
    private String userId;


}
