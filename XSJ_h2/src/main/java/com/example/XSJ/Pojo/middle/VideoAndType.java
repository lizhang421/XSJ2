package com.example.XSJ.Pojo.middle;

import lombok.Data;
import java.util.Date;

/**
 * 视频与类型关联表（纯MyBatis版）
 * 数据库表名：VideoAndType
 */
@Data
public class VideoAndType {
    /**
     * 视频与类型关联表主键
     * 数据库字段：VideoAndTypeId
     */
    private String videoAndTypeId;

    /**
     * 视频表Id
     * 数据库字段：VideoId
     */
    private String videoId;

    /**
     * 类型Id
     * 数据库字段：TypeId
     */
    private String typeId;

    /**
     * 创建时间
     * 数据库字段：CreateTime
     */
    private Date createTime;

    /**
     * 更新时间（自动更新）
     * 数据库字段：UpdateTime
     */
    private Date updateTime;
}