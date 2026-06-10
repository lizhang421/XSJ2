package com.example.XSJ.Pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 池内视频三层选取规则配置表
 * 数据库表名：pool_extract_rule
 */
@Data
public class PoolExtractRule {
    /**
     * 主键ID，自增
     * 数据库字段：id
     */
    private Long id;

    /**
     * 视频分类（如：音乐、美术），通配符*表示所有分类
     * 数据库字段：category
     */
    private String category;

    /**
     * 池类型（如：爆款池、长尾池、冷启动池）
     * 数据库字段：pool_type
     */
    private String poolType;

    /**
     * 上权重层分层占比（0~1）
     * 数据库字段：top_weight_layer_ratio
     */
    private BigDecimal topWeightLayerRatio;

    /**
     * 中权重层分层占比（0~1）
     * 数据库字段：mid_weight_layer_ratio
     */
    private BigDecimal midWeightLayerRatio;

    /**
     * 下权重层分层占比（0~1）
     * 数据库字段：bottom_weight_layer_ratio
     */
    private BigDecimal bottomWeightLayerRatio;

    /**
     * 上权重层抽取占比（0~1）
     * 数据库字段：top_weight_extract_ratio
     */
    private BigDecimal topWeightExtractRatio;

    /**
     * 中权重层抽取占比（0~1）
     * 数据库字段：mid_weight_extract_ratio
     */
    private BigDecimal midWeightExtractRatio;

    /**
     * 下权重层抽取占比（0~1）
     * 数据库字段：bottom_weight_extract_ratio
     */
    private BigDecimal bottomWeightExtractRatio;

    /**
     * 分层占比校验：1=总和=1（合法），0=不合法（无效配置）
     * 数据库字段：layer_ratio_check
     * 说明：数据库生成列，只读不修改
     */
    private Boolean layerRatioCheck;

    /**
     * 抽取占比校验：1=总和=1（合法），0=不合法（无效配置）
     * 数据库字段：extract_ratio_check
     * 说明：数据库生成列，只读不修改
     */
    private Boolean extractRatioCheck;

    /**
     * 上权重层最低权重阈值，0不限制
     * 数据库字段：top_weight_min_threshold
     */
    private Integer topWeightMinThreshold;

    /**
     * 中权重层最低权重阈值，0不限制
     * 数据库字段：mid_weight_min_threshold
     */
    private Integer midWeightMinThreshold;

    /**
     * 下权重层最低权重阈值，0不限制
     * 数据库字段：bottom_weight_min_threshold
     */
    private Integer bottomWeightMinThreshold;

    /**
     * 生效状态：1=生效，0=失效
     * 数据库字段：enable
     */
    private Boolean enable;

    /**
     * 乐观锁版本号（用于解决并发修改冲突）
     * 数据库字段：version
     */
    private Integer version;

    /**
     * 配置备注（如：2026.01.15 音乐爆款池 3:5:2分层 5:3:2抽取）
     * 数据库字段：remark
     */
    private String remark;

    /**
     * 创建人
     * 数据库字段：create_by
     */
    private String createBy;

    /**
     * 创建时间
     * 数据库字段：create_time
     */
    private Date createTime;

    /**
     * 更新人
     * 数据库字段：update_by
     */
    private String updateBy;

    /**
     * 更新时间
     * 数据库字段：update_time
     */
    private Date updateTime;
}