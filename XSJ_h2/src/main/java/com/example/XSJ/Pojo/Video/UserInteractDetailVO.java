package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInteractDetailVO {

    // 互动表主键（保留）
    private String id;

    private String userEncode;
    private String videoEncode;


    private Integer isLike;       // 点赞 0/1
    private Integer isCollect;    // 收藏 0/1
    private Integer isComment;    // 评论 0/1
    private Integer isShare;      // 分享 0/1


    private String videoName;     // 视频标题
    private String videoImageUrl; // 视频封面
    private String videoUrl;      // 视频地址（可选）
    private String authorEncode;  // 视频作者encode
    private String authorName;    // 视频作者昵称

    private Integer playPercent;
    private Long lastPosition;
    private Integer interactScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private  int isRead;
}