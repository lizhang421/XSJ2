package com.example.XSJ.Pojo.UserChat;

import lombok.Data;

@Data
public class ChatSessionVO {
    private String chatSessionId;
    private String sessionCode;
    private String otherEncode;      // 对方 encode
    private String otherName;        // 对方昵称
    private String otherAvatar;      // 对方头像
    private String lastMessage;      // 最后一条消息
    private String lastMessageTime;  // 最后消息时间
    private Integer unreadCount;     // 未读数量（红点）

}