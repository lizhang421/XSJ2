package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 视频审核标签主表
 * 数据库表名：VideoAuditTag
 */
@Data
public class VideoAuditTag {
    /**
     * 标签主键ID
     * 数据库字段：AuditTagId
     */
    private String auditTagId;

    /**
     * 标签名称（如：暴力、低俗）
     * 数据库字段：TagName
     */
    private String tagName;

    /**
     * 标签编码（如：VIOLENCE、LOWBROW）
     * 数据库字段：TagCode
     */
    private String tagCode;

    /**
     * 标签类型（1-违禁 2-合规 3-人工复核）
     * 数据库字段：TagType
     */
    private Integer tagType;

    /**
     * 软删除（0-未删 1-已删）
     * 数据库字段：IsDelete
     */
    private Integer isDelete;

    /**
     * 创建时间
     * 数据库字段：CreateTime
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 数据库字段：UpdateTime
     */
    private LocalDateTime updateTime;
}