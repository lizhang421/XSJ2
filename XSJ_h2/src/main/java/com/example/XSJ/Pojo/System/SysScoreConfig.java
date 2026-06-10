package com.example.XSJ.Pojo.System;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 视频100分制系数配置表
 * 数据库表名：sys_score_config
 */
@Data
public class SysScoreConfig {
    /**
     * 主键ID，自增
     * 数据库字段：Id
     */
    private Long id;

    /**
     * 全局配置标识（固定为*）
     * 数据库字段：Category
     */
    private String category;

    /**
     * 完播率系数（0~100）
     * 数据库字段：FinishRateWeight
     */
    private BigDecimal finishRateWeight;

    /**
     * 点赞率系数（0~100）
     * 数据库字段：LikeRateWeight
     */
    private BigDecimal likeRateWeight;

    /**
     * 评论率系数（0~100）
     * 数据库字段：CommentRateWeight
     */
    private BigDecimal commentRateWeight;

    /**
     * 收藏率系数（0~100）
     * 数据库字段：CollectRateWeight
     */
    private BigDecimal collectRateWeight;

    /**
     * 分享率系数（0~100）
     * 数据库字段：ShareRateWeight
     */
    private BigDecimal shareRateWeight;

    /**
     * 时间衰减系数（0~100）
     * 数据库字段：TimeScoreWeight
     */
    private BigDecimal timeScoreWeight;

    /**
     * 分数统计时间窗口（小时）
     * 数据库字段：ScoreWindowHours
     */
    private Integer scoreWindowHours;

    /**
     * 总权重校验：1=总和=100（合法），0=不合法
     * 数据库字段：TotalWeightCheck
     */
    private Integer totalWeightCheck;

    /**
     * 生效状态：1=生效，0=失效
     * 数据库字段：Enable
     */
    private Integer enable;

    /**
     * 乐观锁版本号
     * 数据库字段：Version
     */
    private Integer version;

    /**
     * 配置备注
     * 数据库字段：Remark
     */
    private String remark;

    /**
     * 创建人
     * 数据库字段：CreateBy
     */
    private String createBy;

    /**
     * 创建时间
     * 数据库字段：CreateTime
     */
    private Date createTime;

    /**
     * 更新人
     * 数据库字段：UpdateBy
     */
    private String updateBy;

    /**
     * 更新时间
     * 数据库字段：UpdateTime
     */
    private Date updateTime;
}