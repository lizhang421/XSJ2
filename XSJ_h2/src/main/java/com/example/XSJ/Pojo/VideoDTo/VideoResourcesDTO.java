package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResourcesDTO {
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
    private Integer downloadCount = 0; // 补充默认值，避免null

    /**
     * 上传时间
     * 数据库字段：UploadTime
     */
    private LocalDateTime uploadTime;

    /**
     * 文件描述（资源说明）
     * 数据库字段：ResourceDesc
     */
    private String resourceDesc;
}
