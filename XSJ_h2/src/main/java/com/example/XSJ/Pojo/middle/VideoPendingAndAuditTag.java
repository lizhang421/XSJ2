package com.example.XSJ.Pojo.middle;

import lombok.Data;

/**
 * 视频前置表与审核标签关联表
 * 数据库表名：VideoPendingAndAuditTag
 */
@Data
public class VideoPendingAndAuditTag {
    /**
     * 关联表主键
     * 数据库字段：VideoPendingAndAuditTagId
     */
    private String videoPendingAndAuditTagId;

    /**
     * 审核标签表主键
     * 数据库字段：AuditTagId
     */
    private String auditTagId;

    /**
     * 视频前置表主键
     * 数据库字段：VideoPendingId
     */
    private String videoPendingId;
}