package com.example.XSJ.Pojo.Ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 用户提问ai
 */
@Data
public class AiChatRequest {


    /**
     * 对话会话ID（不传则自动生成，用于关联同一次对话）
     */
    private String sessionId;

    /**
     * 选择的AI模型标识（如qwen-plus，不传则用默认模型）
     */
    private String modelCode;

    /**
     * 用户提问内容（必传）
     */
    @NotBlank(message = "提问内容不能为空")
    private String content;
}