package com.example.XSJ.Pojo.UserChat;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天会话表
 * 数据库表名：chatsession
 */
@Data
public class ChatSession {
    private String chatSessionId;
    private String sessionCode;
    private String userId;
    private String toUserId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private Integer userIsDelete;
    private Integer toUserIsDelete;
    private String userLastMsgId;
    private String toUserLastMsgId;
    private LocalDateTime createTime;
}