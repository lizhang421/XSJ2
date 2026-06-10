package com.example.XSJ.Pojo.System;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserReview {
    private String id;
    private String userId;
    private String targetType;   // AVATAR / BACKGROUND / VIDEO
    private Integer status;      // 0审核中 1通过 2拒绝
    private String content;      // 备注/原因
    private Integer isRead;      // 0未读 1已读  → 你要的已读
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String RecourseUrl;
}