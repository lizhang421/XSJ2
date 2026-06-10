package com.example.XSJ.Pojo.Video;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 获取回复消息列表
 */
@Data
public class VideoCommentVO {



    private String videoCommentId;  // 评论ID
    private String videoId;         // 视频ID
    private String userId;          // 回复我的用户ID
    private String parentId;        // 父评论ID（你的评论）
    private String content;         // 回复内容
    private Integer likeCount;      // 点赞数
    private Integer status;         // 状态
    private LocalDateTime createTime; // 回复时间
    private Integer isRead;         // 是否已读

    private String replyNickName;   // 回复我的人昵称
    private String replyAvatar;     // 回复我的人头像
    private String replyEncode;     // 回复我的人Encode

    private String originalContent; // 你原来发的评论内容


    private String videoName;       // 视频标题
    private String videoImageUrl;   // 视频封面
    private String videoEncode;     // 视频Encode
}

