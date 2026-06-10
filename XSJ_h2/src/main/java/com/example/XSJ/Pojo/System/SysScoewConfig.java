package com.example.XSJ.Pojo.System;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SysScoewConfig {
    /**
     * 主键
     * 数据库字段：Id
     */
    private Integer id;


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
     * 配置备注
     * 数据库字段：Remark
     */
    private String remark;


    /**
     * 更新人
     * 数据库字段：UpdateBy
     */
    private String updateBy;



}
