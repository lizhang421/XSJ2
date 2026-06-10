package com.example.XSJ.Pojo.Ai;

import lombok.Data;

/**
 * AI模型前端展示专用DTO（仅包含前端可访问的安全字段）
 */
@Data
public class AiConfigFrontDTO {

    /**
     * 模型唯一标识（前端选择后回传后端，如qwen-plus）
     * 数据库字段：ModelCode
     */
    private String modelCode;

    /**
     * 模型名称（给用户展示，如通义千问-增强版）
     * 数据库字段：ModelName
     */
    private String modelName;

    /**
     * 模型状态：1-启用，0-禁用（前端用于控制是否可选择，禁用则灰显）
     * 数据库字段：Status
     */
    private Integer status;

    /**
     * 模型配图（前端展示模型封面/图标）
     * 数据库字段：image
     */
    private String image;

    /**
     * 模型简介（前端展示模型特点，如「响应快、支持长文本」）
     * 数据库字段：introduction
     */
    private String introduction;


}