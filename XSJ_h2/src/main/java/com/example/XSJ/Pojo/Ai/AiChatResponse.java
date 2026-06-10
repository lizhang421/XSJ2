package com.example.XSJ.Pojo.Ai;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI对话响应DTO
 */
@Data
public class AiChatResponse {
    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * AI回复内容
     */
    private String aiContent;

    /**
     * 使用的模型名称
     */
    private String modelName;

    /**
     * 对话时间
     */
    private LocalDateTime chatTime;
}