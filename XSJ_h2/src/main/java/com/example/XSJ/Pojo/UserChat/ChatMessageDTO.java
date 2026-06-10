package com.example.XSJ.Pojo.UserChat;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String toEncode;   // 接收方ID
    private String content;  // 消息内容
    private Integer msgType;// 0文字 1图片 2文件
}