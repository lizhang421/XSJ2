package com.example.XSJ.Pojo.Ai;

import lombok.Data;

/**
 * 流式回复DTO（前端逐块接收）
 */
@Data
public class AiChatStreamResponse {
    private String chunkContent; // 单块内容（一个字/一句话）
    private Boolean isFinish = false; // 是否是最后一块
    private String sessionId;    // 会话ID
    private String modelName;    // 模型名称
}