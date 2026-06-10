package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视频浏览历史表
 * 数据库表名：userViewHistory
 */
@Data
public class UserViewHistory {
    /**
     * 主键ID
     * 数据库字段：id
     */
    private String id;

    /**
     * 用户ID
     * 数据库字段：userId
     */
    private String userId;

    /**
     * 视频ID
     * 数据库字段：videoId
     */
    private String videoId;

    /**
     * 观看时间
     * 数据库字段：watchTime
     */
    private LocalDateTime watchTime;

    /**
     * 本次观看时长（秒）
     * 数据库字段：duration
     */
    private Integer duration;

    /**
     * 软删除（0-未删 1-已删）
     * 数据库字段：IsDelete
     */
    private Integer isDelete;

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