package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.util.Date;

/**
 * 数据库表名：VideoType
 */
@Data
public class VideoType {
    /**
     * 主键id
     * 数据库字段：VideoTypeId
     */
    private String videoTypeId;

    /**
     * 代码（唯一约束）
     * 数据库字段：TypeCode
     */
    private String typeCode;

    /**
     * 类型名称
     * 数据库字段：TypeName
     */
    private String typeName;

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