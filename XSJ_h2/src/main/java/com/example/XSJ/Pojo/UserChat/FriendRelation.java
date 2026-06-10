package com.example.XSJ.Pojo.UserChat;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 好友关系表
 * 数据库表名：
 */
@Data
public class FriendRelation {
    /**
     * 主键
     * 数据库字段：friendRelationId
     */
    private String friendRelationId;

    /**
     * 用户ID/用户唯一标识
     * 数据库字段：userEncode
     */
    private String userId;

    /**
     * 好友ID
     * 数据库字段：friendEncode
     */
    private String friendId;

    private String userRemark;    // UserId 给 FriendId 的备注
    private String friendRemark;

    /**
     * //1是同意 2 是删除 3是待确认，4是不同意，
     * 数据库字段：status
     */
    private Integer status;

    /**
     * 创建时间
     * 数据库字段：createTime
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 数据库字段：updateTime
     */
    private LocalDateTime updateTime;
}