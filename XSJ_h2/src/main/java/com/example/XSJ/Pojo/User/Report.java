package com.example.XSJ.Pojo.User;

import lombok.Data;
import java.util.Date;

@Data
public class Report {
    // 主键ID(字符串)
    private String id;

    // 资源类型 01视频 02评论
    private int reportType;

    // 被举报资源ID(视频/评论ID)
    private String targetId;

    // 举报人ID
    private String createUserId;

    // 被举报人ID
    private String targetUserId;

    // 举报原因+备注
    private String reason;
    private String remark;

    // 后台处理状态
    private Integer status;

    // 举报人已读 / 被举报人已读
    private Integer userIsRead;
    private Integer targetUserIsRead;

    // 时间
    private Date createTime;
    private Date updateTime;
}