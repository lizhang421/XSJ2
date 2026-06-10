package com.example.XSJ.Pojo.Video;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResources {


    /**
     * 资源主键ID
     * 数据库字段：VideoResourcesId
     */
    private String videoResourcesId;

    /**
     * 关联视频表的主键ID
     * 数据库字段：VideoId
     */
    private String videoId;

    /**
     * 资源名称（如：Java核心知识点.pdf）
     * 数据库字段：ResourceName
     */
    private String resourceName;

    /**
     * 资源类型（枚举：DOCUMENT/CODE/MEDIA/OTHER）
     * 数据库字段：ResourceType
     */
    private String resourceType;

    /**
     * 文件格式（如：pdf、java、png、mp4）
     * 数据库字段：ResourceFormat
     */
    private String resourceFormat;

    /**
     * 资源存储地址（云存储URL/本地路径）
     * 数据库字段：ResourceUrl
     */
    private String resourceUrl;

    /**
     * 资源封面/缩略图Url（仅图片/PDF可用）
     * 数据库字段：ResourceThumUrl
     */
    private String resourceThumUrl;

    /**
     * 资源大小（字节）
     * 数据库字段：ResourceSize
     */
    private Long resourceSize;

    /**
     * 资源下载数量
     * 数据库字段：DownloadCount
     */
    private Integer downloadCount;

    /**
     * 上传时间
     * 数据库字段：UploadTime
     */
    private LocalDateTime uploadTime;

    /**
     * 上传人ID（视频发布人/文件上传人）
     * 数据库字段：UserId
     */
    private String userId;

    /**
     * 是否删除（0-未删 1-已删）
     * 数据库字段：IsDelete
     */
    private Integer isDelete;

    /**
     * 是否公开（0-仅管理员可见 1-所有人可见）
     * 数据库字段：IsPublic
     */
    private Integer isPublic;

    /**
     * 文件描述（资源说明）
     * 数据库字段：ResourceDesc
     */
    private String resourceDesc;


}
