package com.example.XSJ.Pojo.Video;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoComment {

    /**
     * 评论ID（主键）
     * 数据库字段：VideoCommentId
     */
    private String videoCommentId;

    /**
     * 关联视频ID
     * 数据库字段：VideoId
     */
    private String videoId;

    /**
     * 评论发布者ID
     * 数据库字段：UserId
     */
    private String userId;

    /**
     * 父评论ID，空字符串表示根评论（直接评论视频）
     * 数据库字段：ParentId
     */
    private String parentId = "";

    /**
     * 评论内容（扩容到2000字符，满足长评论需求）
     * 数据库字段：Content
     */
    private String content;

    /**
     * 点赞数，默认0
     * 数据库字段：LikeCount
     */
    private Integer likeCount = 0;

    /**
     * 评论状态：1-正常，2-屏蔽，3-删除
     * 数据库字段：Status
     */
    private Integer status = 1;

    /**
     * 评论创建时间
     * 数据库字段：CreateTime
     */
    private LocalDateTime createTime;


    /**
     * 这条信息是否已读
     * 数据库字段：IsRead
     */
    private Integer isRead ;

}