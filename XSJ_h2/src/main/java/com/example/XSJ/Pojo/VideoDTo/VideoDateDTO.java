package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

@Data
public class VideoDateDTO {
    /**
     * 播放量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    //--------  当前用户做出的操作

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
     * 上次播放位置（秒）
     * 数据库字段：lastPosition
     */
    private Integer lastPosition;

}
