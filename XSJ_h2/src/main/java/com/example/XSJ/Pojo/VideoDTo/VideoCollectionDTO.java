package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 视频集合信息DTO
 * 字段对应videogroup表核心字段
 */
@Data
public class VideoCollectionDTO {
    /**
     * 集合ID
     * 数据库字段：CollectionId
     */
    private String collectionId;

    /**
     * 集合名称
     * 数据库字段：CollectionName
     */
    private String collectionName;

    /**
     * 集合封面地址
     * 数据库字段：CollectionImageUrl
     */
    private String collectionImageUrl;

    /**
     * 集合描述
     * 数据库字段：CollectionDesc
     */
    private String collectionDesc;

    /**
     * 集合创建者ID
     * 数据库字段：CreatorId
     */
    private String creatorId;

    /**
     * 集合创建者名称
     * 数据库字段：CreatorName
     */
    private String creatorName;

    /**
     * 集合内视频总数
     * 数据库字段：VideoCount
     */
    private Integer videoCount = 0;

    /**
     * 集合是否删除（0-未删除，1-已删除）
     * 数据库字段：IsDelete
     */
    private Integer isDelete = 0;

    /**
     * 集合状态（0-正常，1-下架，2-审核中）
     * 数据库字段：CollectionStatus
     */
    private Integer collectionStatus = 0;

    /**
     * 集合创建时间
     * 数据库字段：CreateTime
     */
    private LocalDateTime createTime;

    /**
     * 集合更新时间
     * 数据库字段：UpdateTime
     */
    private LocalDateTime updateTime;
}