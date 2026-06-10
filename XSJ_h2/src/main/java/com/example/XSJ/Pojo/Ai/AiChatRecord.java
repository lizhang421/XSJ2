package com.example.XSJ.Pojo.Ai;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * MongoDB 聊天记录实体
 */
@Data
@Document(collection = "ai_chat_records") // 对应MongoDB的集合名
public class AiChatRecord {
    /**
     * MongoDB主键（自动生成ObjectId）
     */
    @Id
    private String id;

    /**
     * 用户ID（关联你的用户表）
     */
    private String userId;

    /**
     * 对话会话ID（用于区分同一次连续对话，比如UUID）
     */
    private String sessionId;

    /**
     * AI模型标识（关联MySQL的aiconfig表ModelCode）
     */
    private String modelCode;

    /**
     * 角色：user（用户）/ai（AI回复）
     */
    private String role;

    /**
     * 对话内容（用户提问/AI回复）
     */
    private String content;

    /**
     * 对话时间
     */
    private LocalDateTime chatTime;

    /**
     * 扩展字段：是否删除（软删）
     */
    private Boolean isDeleted = false;
}