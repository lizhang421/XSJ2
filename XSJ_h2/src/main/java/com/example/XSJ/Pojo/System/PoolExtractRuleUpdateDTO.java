package com.example.XSJ.Pojo.System;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 池内视频三层选取规则 修改 DTO
 * 用于接口接收前端修改参数
 */
@Data
public class PoolExtractRuleUpdateDTO {

    /**
     * 主键ID【必传】
     */
    private Long id;

    /**
     * 视频分类 *代表全部
     */
    private String category;

    /**
     * 池类型：爆款池/长尾池/冷池/冷启动池
     */
    private String poolType;

    // ====================== 分层占比 ======================
    /**
     * 上权重层 分层占比
     */
    private BigDecimal topWeightLayerRatio;

    /**
     * 中权重层 分层占比
     */
    private BigDecimal midWeightLayerRatio;

    /**
     * 下权重层 分层占比
     */
    private BigDecimal bottomWeightLayerRatio;

    // ====================== 抽取占比 ======================
    /**
     * 上权重层 抽取占比
     */
    private BigDecimal topWeightExtractRatio;

    /**
     * 中权重层 抽取占比
     */
    private BigDecimal midWeightExtractRatio;

    /**
     * 下权重层 抽取占比
     */
    private BigDecimal bottomWeightExtractRatio;

    // ====================== 权重阈值 ======================
    private Integer topWeightMinThreshold;
    private Integer midWeightMinThreshold;
    private Integer bottomWeightMinThreshold;

    // ====================== 状态 ======================
    /**
     * 启用状态 1=启用 0=禁用
     */
    private Boolean enable;

    /**
     * 备注
     */
    private String remark;

}