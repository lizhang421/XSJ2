package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户-视频互动状态表
 * 数据库表名：userVideoInteract
 */
@Data
public class UserVideoInteract {
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
     * 视频作者ID
     * 数据库字段：authorId
     */
    private String authorId;

    /**
     * 是否点赞（0-未点赞 1-已点赞）
     * 数据库字段：isLike
     */
    private Integer isLike;

    /**
     * 是否收藏（0-未收藏 1-已收藏）
     * 数据库字段：isCollect
     */
    private Integer isCollect;

    /**
     * 是否评论（0-未评论 1-已评论）
     * 数据库字段：isComment
     */
    private Integer isComment;

    /**
     * 是否转发（0-未转发 1-已转发）
     * 数据库字段：isShare
     */
    private Integer isShare;

    /**
     * 播放进度（百分比 0-100）
     * 数据库字段：playPercent
     */
    private Integer playPercent;

    /**
     * 上次播放位置（秒）
     * 数据库字段：lastPosition
     */
    private Integer lastPosition;

    /**
     * 互动总分（推荐算法用）
     * 数据库字段：interactScore
     */
    private Integer interactScore;

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