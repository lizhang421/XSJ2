package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 视频分发权重记录表
 * 数据库表名：VideoDistributionWeight
 */
@Data
public class VideoDistributionWeight {
    /**
     * 视频分发权重记录表主键
     * 数据库字段：VideoDistributionWeightId
     */
    private String videoDistributionWeightId;

    /**
     * 用户Id
     * 数据库字段：UserId
     */
    private String userId;

    /**
     * 视频Id
     * 数据库字段：VideoId
     */
    private String videoId;

    /**
     * 最终个性化权重（0-1000，全局权重 × 用户标签匹配度 × 池流量系数）
     * 数据库字段：FinaPersonalWeight
     */
    private BigDecimal finaPersonalWeight;

    /**
     * 所属池
     * 数据库字段：PoolFullName
     */
    private String poolFullName;

    /**
     * 分发时间
     * 数据库字段：DistributionTime
     */
    private Date distributionTime;

    /**
     * 用户是否查看0没有 1看了
     * 数据库字段：UserFeedbackFlag
     */
    private Integer userFeedbackFlag;

    /**
     * 用户查看反馈
     * 数据库字段：FeedbackDetail
     */
    private String feedbackDetail;
}