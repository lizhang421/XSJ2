package com.example.XSJ.Pojo.Ai;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddAiConfig {



    /**
     * 模型唯一标识（如qwen-plus、qwen-turbo）
     * 数据库字段：ModelCode
     */
    private String modelCode;

    /**
     * 模型名称（给用户展示，如通义千问-增强版）
     * 数据库字段：ModelName
     */
    private String modelName;

    /**
     * API密钥（如sk-7d99f8b407354665816eab1507c68b19）
     * 数据库字段：ApiKey
     */
    private String apiKey;

    /**
     * AI接口地址（如https://dashscope.aliyuncs.com/...）
     * 数据库字段：BaseUrl
     */
    private String baseUrl;

    /**
     * 模型参数（接口调用时传的model值，如qwen-plus）
     * 数据库字段：Model
     */
    private String model;

    /**
     * 状态：1-启用，0-禁用
     * 数据库字段：Status
     * 【默认值】1（启用）
     */
    private Integer status = 1;


    /**
     * 图片
     */
    private String image;
    /**
     * 简介
     */
    private String introduction;
}
