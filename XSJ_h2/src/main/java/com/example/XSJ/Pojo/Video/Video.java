package com.example.XSJ.Pojo.Video;

import cn.hutool.json.JSON;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 视频信息实体类
 * 对应数据库视频主表
 */
@Data

public class Video  {


    /**
     * 视频主键id
     */
    private String videoId;

    /**
     * 视频唯一编码
     */
    private String enCode;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 视频封面地址
     */
    private String videoImageUrl;

    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 发布人id
     */
    private String publisherId;

    /**
     * 审核状态（审核状态等）
     */
    private Integer reviewStatus;

    /**
     * 是否属于集合
     */
    private Integer isCollection;

    /**
     * 所属集合id
     */
    private String collectionId;

    /**
     * 视频分辨率
     */
    private String videoResolution;

    /**
     * 视频时长（这里注意：原表字段是VideoTime，建议存储秒数，类型为Integer）
     */
    private Integer videoTime;

    /**
     * 视频文件大小（字节，原表字段VideoSize，修正为Long类型）
     */
    private Long videoSize;

    /**
     * 视频格式
     */
    private String videoFormat;

    /**
     * 审核人id
     */
    private String reviewerId;

    /**
     * 审核备注
     */
    private String reviewerRemark;

    /**
     * 排序权重
     */
    private String sortWeight;

    /**
     * 播放量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 是否精选
     */
    private Integer isFeature;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 视频状态（正常，封禁，下架等）
     */
    private Integer videoStatus;

    /**
     * 是否需要会员（0不需要，1需要）
     */
    private Integer isMember;

    /**
     * 是否精选（成为官方精选之后可以认证）
     */
    private Integer isSelect;
    /**
     * 视频全局权重
     */
    private  BigDecimal GlobalBaseWeight;
    /**
     * 冷启动核心数据，json存储用户在冷启动期间的表现，用户在v冷启动期间会强制指定一些
     * 用户，根据冷启动之间的数据进行分配，这个规则可以告诉给用户，让用户明白细节规则
     */
    private Map<String,Object> ColdStartData;
    /**
     * 视频当前归属池，视频是在哪个归属池里面
     */
    private  String CurrentPoolType;

    /**
     * 简介
     */
    private String introduction;

}