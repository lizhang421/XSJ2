package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 视频池配置表（纯MyBatis版）
 * 数据库表名：VideoPoolConfig
 */
@Data
public class VideoPoolConfig {
    /**
     * 视频池主键配置类主键id
     * 数据库字段：PoolId
     */
    private String VideoPoolConfigId;

    /**
     * 池所属品类
     * 数据库字段：PoolCaetGory
     */
    private String poolCaetGory;

    /**
     * 池形
     * 数据库字段：PoolType
     */
    private String poolType;

    /**
     * 池全称
     * 数据库字段：PoolFullName
     */
    private String poolFullName;

    /**
     * 入池规则
     * 数据库字段：EntryRules
     */
    private String entryRules;

    /**
     * 该池流量分配比例
     * 数据库字段：TrafficRatio
     */
    private BigDecimal trafficRatio;

    /**
     * 池状态
     * 数据库字段：PoolStatus
     */
    private Integer poolStatus;

    /**
     * 配置创建时间
     * 数据库字段：CreateTime
     */
    private Date createTime;

    /**
     * 更新时间
     * 数据库字段：UpdateTime
     */
    private Date updateTime;

    /**
     *
     * 池抽取比例
     */

    private BigDecimal GetPoolMount;
}