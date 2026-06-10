package com.example.XSJ.Pojo.User;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInteractMsgVO {

    private String interactId;

    private String videoId;

    private String fromUserId;

    private String parentId;

    private String content;


    private Integer likeCount;

    private Integer status;


    private LocalDateTime createTime;


    private Integer isRead;


    private String fromNickName;


    private String fromAvatar;


    private String fromEncode;


    private String originalContent;


    private String videoName;

    // 视频封面（不变）
    private String videoImageUrl;

    // 视频Encode（不变）
    private String videoEncode;

    // ================= 互动类型（新增关键字段） =================
    // LIKE-点赞 COLLECT-收藏 COMMENT-评论 SHARE-分享
    private String interactType;
}