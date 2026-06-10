package com.example.XSJ.Pojo.User;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportVO {
    // 举报主表全量基础字段
    private String id;
    private String createUserId;
    private String targetUserId;
    private Integer reportType;
    private String targetId;
    private Integer reasonId;
    private String reason;
    private String remark;
    private Integer status;
    private Integer userIsRead;       // 举报人已读
    private Integer targetUserIsRead; // 被举报人已读
    private LocalDateTime createTime;

    // 视频
    private String enCode;
    private String videoName;
    private String videoImageUrl;

    // 评论
    private String commentContent;
}