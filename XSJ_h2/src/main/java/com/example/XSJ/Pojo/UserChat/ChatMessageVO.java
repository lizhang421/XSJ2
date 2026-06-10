package com.example.XSJ.Pojo.UserChat;

import lombok.Data;

@Data
public class ChatMessageVO {
    private String msgId;          // 消息唯一ID
    private String fromEncode;     // 发送者 encode（前端唯一识别码）
    private String toEncode;       // 接收者 encode
    private String content;        // 消息内容
    private String sendTime;       // 时间：yyyy-MM-dd HH:mm:ss
    private Integer msgType;       // 0=文字
    private Integer isRead;        // 0=未读 1=已读
}