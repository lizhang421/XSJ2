package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class VideoPendingDTO {
    /**
     * 主键id
     * 数据库字段：VideoPendingId
     */
    private String videoPendingId;

    /**
     * 视频编码
     * 数据库字段：EnCode
     */
    private String enCode;

    /**
     * 视频名称/标题
     * 数据库字段：VideoName
     */
    private String videoName;

    /**
     * 视频描述简介
     * 数据库字段：VideoDesc
     */
    private String videoDesc;

    /**
     * 视频默认地址（封面）
     * 数据库字段：VideoImageUrl
     */
    private String videoImageUrl;

    /**
     * 视频地址
     * 数据库字段：VideoUrl
     */
    private String videoUrl;

    /**
     * 发布人id
     * 数据库字段：PublisherId
     */
    private String publisherId;

    /**
     * 发布人代码
     * 数据库字段：PublishCode
     */
    private String publishCode;

    /**
     * 审核步骤（0-等待 1-进行中 2-完成）
     * 数据库字段：ReviewStatus
     */
    private Integer reviewStatus;

    /**
     * 上传时间
     * 数据库字段：PublishTime
     */
    private LocalDateTime publishTime;

    /**
     * 视频时长（建议改为Integer类型，单位秒）
     * 数据库字段：VideoTime
     */
    private int videoTime;

    /**
     * 视频文件大小（字节）
     * 数据库字段：VideoSize
     */
    private Integer videoSize;

    /**
     * 视频格式
     * 数据库字段：VideoFormat
     */
    private String videoFormat;

    /**
     * 是否属于集合（0-否 1-是）
     * 数据库字段：IsCollection
     */
    private Integer isCollection;

    /**
     * 所属集合id
     * 数据库字段：CollectionId
     */
    private Integer collectionId;

    /**
     * 视频状态（1-通过 0-违禁 2-等待结果）
     * 数据库字段：VideoStaus → 修正为VideoStatus（原表拼写错误）
     */
    private Integer videoStatus;

    /**
     * 开始审核时间
     * 数据库字段：AuditTime
     */
    private LocalDateTime auditTime;

    /**
     * 审核结束时间
     * 数据库字段：EndAuditTime
     */
    private LocalDateTime endAuditTime;

    /**
     * 软删除（0-未删 1-已删）
     * 数据库字段：IsDelete
     */
    private Integer isDelete;

    /**
     * 审核人代码
     * 数据库字段：ReviewerCode
     */
    private String reviewerCode;

    /**
     * 审核人id
     * 数据库字段：ReviewerId
     */
    private String reviewerId;

    /**
     * 创建时间
     * 数据库字段：CreateTime
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（自动更新）
     * 数据库字段：UpdateTime
     */
    private LocalDateTime updateTime;


    private String videoType;

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



}
